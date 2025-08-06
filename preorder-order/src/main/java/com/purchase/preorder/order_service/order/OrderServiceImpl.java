package com.purchase.preorder.order_service.order;

import com.common.core.util.JwtParser;
import com.common.domain.common.OrderStatus;
import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.OrderItem;
import com.common.domain.entity.order.projection.OrderPaidInfo;
import com.common.domain.repository.order.OrderRepository;
import com.common.event_common.domain_event_vo.order.*;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByCancelDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByReturnDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByRollbackDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRollbackRequestedDomainEvent;
import com.common.event_common.mapper.OrderDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.common.kafka.event_vo.payment.CancelReason;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.order_service.api.internal.ItemClient;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.*;
import com.purchase.preorder.order_service.order_item.service.OrderItemService;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.common.core.exception.ExceptionCode.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final ItemClient itemClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final JwtUtils jwtUtils;
    private final OrderDomainEventMapper mapper;
    private final DomainEventPublisher publisher;
    private final Executor executor;

    // TODO 배송 서비스 분리할 경우 삭제
    private final ShipmentService shipmentService;

    public OrderServiceImpl(ItemClient itemClient,
                            OrderRepository orderRepository,
                            OrderItemService orderItemService,
                            JwtUtils jwtUtils,
                            OrderDomainEventMapper mapper,
                            @Qualifier("orderDomainEventPublisher") DomainEventPublisher publisher,
                            @Qualifier("feignClientTaskExecutor") Executor executor,
                            ShipmentService shipmentService) {
        this.itemClient = itemClient;
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.jwtUtils = jwtUtils;
        this.mapper = mapper;
        this.publisher = publisher;
        this.executor = executor;
        this.shipmentService = shipmentService;
    }

    @Override
    public ResOrderDto createOrder(ReqOrderDto req, HttpServletRequest request) {
        // 1. 유저 인증 (JWT로 이메일 가져오기)
        Long userId = getUserIdOfAuthenticatedUser(request);

        List<Long> orderRequestedItemIds = req.getOrderItemList().stream()
                .map(ReqOrderItemDto::getItemId)
                .distinct()
                .toList();

        // 2. 재고 조회 및 재고 임시 차감
        List<ReqReserveStockDto> reserveStockDtos = req.getOrderItemList().stream()
                .map(oi -> ReqReserveStockDto.of(oi.getItemId(), oi.getItemCount()))
                .toList();

        // 3. 상품 조회 비동기 진행
        CompletableFuture<List<ItemResponse>> itemFut = CompletableFuture
                .supplyAsync(() -> itemClient.getItems(orderRequestedItemIds), executor);

        // 4. 재고 선점은 동기 처리
        try {
            itemClient.reserveStocks(userId, reserveStockDtos);
        } catch (FeignException ex) {
            // 실패 시 조회 중인 future 취소
            itemFut.cancel(true);
            log.error("[ORDER-SERVICE] 재고 선점 실패 - 원인: {}", ex.getMessage());
            throw ex;
        }

        // 4. 상품 조회 결과 기다리기
        List<ItemResponse> items;
        try {
            items = itemFut.join();
        } catch (CompletionException ce) {
            throw new BusinessException(ce.getMessage(), INTERNAL_SERVER_ERROR);
        }

        // 6. DB 저장 및 리턴
        return saveOrderTransactionally(req, userId, items);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResOrderDto> readAllOrder(HttpServletRequest request, Integer page, Integer size) throws Exception {
        Long userId = getUserIdOfAuthenticatedUser(request);

        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserId(userId, pageable).map(ResOrderDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ResOrderDto readOrder(HttpServletRequest request, Long orderId) throws Exception {
        Long userId = getUserIdOfAuthenticatedUser(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(userId, order.getUserId());

        return ResOrderDto.fromEntity(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) throw new BusinessException(NOT_FOUND_ORDER);

        orderRepository.deleteAll(orders);

        for (Order order : orders) {
            List<Long> orderItemIds = order.getOrderItemList().stream().map(OrderItem::getId).toList();
            OrderDeletedDomainEvent event = mapper.toOrderDeletedEvent(userId, order.getId(), orderItemIds);
            publisher.publishWithOutboxAfterCommit(event);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        orderItemService.updateStatus(order.getOrderItemList(), status.toString());
        order.updateStatus(status);
        log.info("주문 상태 변경 - 상태: {}", status);

        if (status == OrderStatus.CANCELED || status == OrderStatus.RETURNED) {
            Map<Long, Integer> stockMap = order.getOrderItemList().stream()
                    .collect(Collectors.toMap(OrderItem::getItemId, OrderItem::getQuantity));
            StockRollbackRequestedDomainEvent event = mapper.toStockRollbackRequestedEvent(order.getId(), stockMap);
            publisher.publishWithOutboxAfterCommit(event);
        } else if (status == OrderStatus.PAID) {
            List<Long> orderItemIds = order.getOrderItemList().stream().map(OrderItem::getId).toList();
            OrderCompletedDomainEvent event = mapper.toOrderCompletedEvent(orderId, orderItemIds);
            publisher.publishOnlySpringEventAfterCommit(event);
        }
    }

    @Override
    @Transactional
    public void updateStatusByRollback(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        orderItemService.updateStatus(order.getOrderItemList(), status.toString());
        order.updateStatus(status);
        log.info("주문 상태 변경 - 상태: {}", status);

        OrderCompensationCompletedDomainEvent event = mapper.toOrderCompensationCompletedEvent(
                orderId, order.getOrderItemList().stream().map(OrderItem::getId).toList()
        );
        publisher.publishOnlySpringEventAfterCommit(event);
    }

    @Override
    @Transactional
    public void assignShipments(Long orderId, Map<Long, Long> shipmentMap) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        orderItemService.assignShipments(order.getOrderItemList(), shipmentMap);
    }

    @Override
    @Transactional
    public void cancelOrder(HttpServletRequest request, Long orderId, ReqCancelOrderDto req) throws Exception {
        Long userId = getUserIdOfAuthenticatedUser(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(userId, order.getUserId());

        if (shipmentService.validateCancelable(order.getOrderItemList().stream().map(OrderItem::getId).toList())) {
            throw new BusinessException(ALREADY_SHIPPING);
        }

        orderItemService.requestCancel(req.getOrderItemIds());
        order.updateStatus(OrderStatus.CANCEL_REQUESTED);

        OrderCancelRequestedDomainEvent event = mapper.toOrderCancelRequestedEvent(order.getId(), req.getOrderItemIds(), req.getCancelReason());
        publisher.publishOnlySpringEventAfterCommit(event);
    }

    @Override
    @Transactional
    public void returnOrder(HttpServletRequest request, Long orderId, List<Long> orderItemIds) throws Exception {
        Long userId = getUserIdOfAuthenticatedUser(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(userId, order.getUserId());

        if (shipmentService.validateReturnable(order.getOrderItemList().stream().map(OrderItem::getId).toList())) {
            throw new BusinessException(NO_RETURN);
        }

        orderItemService.requestReturn(orderItemIds);
        order.updateStatus(OrderStatus.RETURN_REQUESTED);

        OrderReturnRequestedDomainEvent event = mapper.toOrderReturnRequestedEvent(order.getId(), orderItemIds);
        publisher.publishOnlySpringEventAfterCommit(event);
    }

    @Override
    @Transactional
    public void onPaymentSucceed(Long orderId) {
        OrderPaidInfo orderPaidInfo = orderRepository.findOrderPaidInfoById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        OrderPaidDomainEvent domainEvent = mapper.toOrderPaidEvent(orderPaidInfo);
        publisher.publishWithOutboxAfterCommit(domainEvent);
    }

    @Override
    @Transactional
    public void onPaymentFailure(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        order.updateStatus(OrderStatus.PAYMENT_FAILED);
        orderItemService.updateStatus(order.getOrderItemList(), OrderStatus.PAYMENT_FAILED.name());

        OrderPaymentFailedDomainEvent domainEvent = mapper.toOrderPaymentFailedEvent(order);
        publisher.publishWithOutboxAfterCommit(domainEvent);
    }

    @Override
    @Transactional
    public void onShipmentCancel(Long orderId, String cancelReason) {
        PaymentCancelRequestedByCancelDomainEvent event = mapper.toPaymentCancelRequestedByCancelEvent(orderId, cancelReason);
        publisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional
    public void onShipmentReturn(Long shipmentId, Long orderItemId, String cancelReason) {
        Long orderId = orderItemService.findOrderIdByOrderItemId(orderItemId);
        PaymentCancelRequestedByReturnDomainEvent event = mapper.toPaymentCancelRequestedByReturnEvent(shipmentId, orderId, cancelReason);
        publisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional
    public void onRedisRolledBack(Long orderId) {
        PaymentCancelRequestedByRollbackDomainEvent event = mapper.toPaymentCancelRequestedByRollbackEvent(
                orderId, CancelReason.INTERNAL_SERVER_ISSUE.getLabel());
        publisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional
    public void updateStatusByShipment(Long shipmentId, String status) {
        Long orderId = orderItemService.updateStatusByShipment(shipmentId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            log.warn("무효한 status - 원인: {}", ex.getMessage());
            throw new IllegalArgumentException(ex);
        }

        if (!order.getStatus().equals(newStatus)) order.updateStatus(newStatus);
    }

    private void checkMatchesUser(Long userId, Long userIdOfConnectingUser) {
        if (!userId.equals(userIdOfConnectingUser)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }

    private Long getUserIdOfAuthenticatedUser(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));
        return JwtParser.getUserId(accessToken);
    }

    @Transactional
    public ResOrderDto saveOrderTransactionally(ReqOrderDto req, Long userId, List<ItemResponse> items) {
        Map<Long, ItemResponse> itemMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::getId, Function.identity()));
        int totalPrice = req.getOrderItemList().stream()
                .mapToInt(oi -> itemMap.get(oi.getItemId()).getPrice() * oi.getItemCount())
                .sum();

        Order order = orderRepository.save(Order.of(userId, totalPrice));

        List<OrderItem> orderItems = orderItemService.createOrderItems(order, req.getOrderItemList(), itemMap);
        order.addAllOrderItems(orderItems);

        return ResOrderDto.fromEntity(order);
    }
}