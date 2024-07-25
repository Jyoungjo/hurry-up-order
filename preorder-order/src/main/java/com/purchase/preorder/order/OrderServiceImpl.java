package com.purchase.preorder.order;

import com.purchase.preorder.client.ItemClient;
import com.purchase.preorder.client.PaymentClient;
import com.purchase.preorder.client.ReqPaymentDto;
import com.purchase.preorder.client.UserClient;
import com.purchase.preorder.client.response.ItemResponse;
import com.purchase.preorder.client.response.PaymentResponse;
import com.purchase.preorder.client.response.UserResponse;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order.dto.ReqLimitedOrderDto;
import com.purchase.preorder.order.dto.ReqOrderDto;
import com.purchase.preorder.order.dto.ResOrderDto;
import com.purchase.preorder.order_item.OrderItemService;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.purchase.preorder.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final ItemClient itemClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    // 일반 상품들의 결제 프로세스는 우선 제외한다.
    @Override
    @Transactional
    public ResOrderDto createOrder(ReqOrderDto req, HttpServletRequest request) throws Exception {
        // TODO 주문할 때, 재고 없으면 주문 못하도록 막기 + 결제 API 호출하기
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        // Order 객체 생성
        int totalPrice = req.getOrderItemList().stream().mapToInt(oi -> oi.getItemCount() * oi.getPrice()).sum();
        Order order = orderRepository.save(Order.of(user.getId(), totalPrice));

        // OrderItem 객체 생성 및 저장 로직
        orderItemService.createOrderItem(order, req.getOrderItemList());
        Order savedOrder = orderRepository.save(order);

        // Order save
        return ResOrderDto.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public ResOrderDto createOrderOfLimitedItem(ReqLimitedOrderDto req, HttpServletRequest request) throws Exception {
        // 재고 체크
        checkQuantityOfStock(req);

        // 오픈 시간 체크
        ItemResponse foundItem = itemClient.getItem(req.getItemId());
        if (LocalDateTime.now().isBefore(foundItem.getOpenTime())) {
            throw new BusinessException(NOT_REACHED_OPEN_TIME);
        }

        // 접속 유저 정보 불러오기
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);
        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        // Order 객체 생성
        Order order = orderRepository.save(Order.of(user.getId(), req.getPrice()));

        // 결제 시도
        PaymentResponse initiatedPayment = initiatePayment(req, order);

        // 결제 완료
        completePayment(req, initiatedPayment.getPaymentId());

        // OrderItem 객체 생성 및 저장 로직
        orderItemService.createOrderItem(foundItem, order);
        Order savedOrder = orderRepository.save(order);

        // Order save
        return ResOrderDto.fromEntity(savedOrder);
    }

    @Override
    public Page<ResOrderDto> readAllOrder(HttpServletRequest request, Integer page, Integer size) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Pageable pageable = PageRequest.of(page, size);

        return orderRepository.findByUserId(user.getId(), pageable).map(ResOrderDto::fromEntity);
    }

    @Override
    public ResOrderDto readOrder(HttpServletRequest request, Long orderId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        return ResOrderDto.fromEntity(order);
    }

    @Override
    @Transactional
    public void deleteOrder(HttpServletRequest request, Long orderId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void cancelOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderItemService.cancelOrder(order, itemId);
    }

    @Override
    @Transactional
    public void returnOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderItemService.requestReturnOrder(order, itemId);
    }

    private void checkMatchesUser(Long userId, Long userIdOfConnectingUser) {
        if (!userId.equals(userIdOfConnectingUser)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) throws Exception {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
        return AesUtils.aesCBCEncode(JwtParser.getEmail(accessToken));
    }

    private PaymentResponse initiatePayment(ReqLimitedOrderDto req, Order order) {
        itemClient.decreaseStock(req.getItemId(), 1);

        PaymentResponse initiatedPayment = paymentClient.initiatePayment(
                new ReqPaymentDto(order.getId(), req.getPrice()));

        if (!initiatedPayment.getIsSuccess()) {
            itemClient.increaseStock(req.getItemId(), 1);
            throw new BusinessException(CANCEL_PAYMENT);
        }

        return initiatedPayment;
    }

    private void completePayment(ReqLimitedOrderDto req, Long paymentId) {
        PaymentResponse completePayment = paymentClient.completePayment(paymentId);
        if (!completePayment.getIsSuccess()) {
            itemClient.increaseStock(req.getItemId(), 1);
            throw new BusinessException(CANCEL_PAYMENT);
        }
    }

    private void checkQuantityOfStock(ReqLimitedOrderDto req) {
        int stock = itemClient.getStock(req.getItemId()).getQuantity();
        if (stock == 0) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }
    }
}