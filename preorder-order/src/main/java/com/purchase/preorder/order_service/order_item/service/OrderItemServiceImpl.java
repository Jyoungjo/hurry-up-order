package com.purchase.preorder.order_service.order_item.service;

import com.common.core.exception.ExceptionCode;
import com.common.domain.common.OrderItemStatus;
import com.common.domain.common.PaymentStatus;
import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.OrderItem;
import com.common.domain.entity.order.projection.OrderItemOrderIdOnly;
import com.common.domain.repository.order.OrderItemRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.ReqOrderItemDto;
import com.purchase.preorder.order_service.order_item.repository.OrderItemJDBCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemJDBCRepository jdbcRepository;

    @Override
    public List<OrderItem> createOrderItems(Order order, List<ReqOrderItemDto> orderItemList, Map<Long, ItemResponse> itemMap) {
        List<OrderItem> savedOrderItems = new ArrayList<>();

        orderItemList.forEach(orderItemDto -> {
            ItemResponse foundItem = itemMap.get(orderItemDto.getItemId());
            validateReservedItem(foundItem);
            OrderItem orderItem = OrderItem.of(
                    order, foundItem.getId(), orderItemDto.getItemCount(), foundItem.getPrice(), null);
            savedOrderItems.add(orderItem);
        });

        return jdbcRepository.saveAll(savedOrderItems);
    }

    /*
        itemIds: 주문 취소 상품 id list
    */
    @Override
    public void requestCancel(List<Long> orderItemIds) {
        List<OrderItem> cancelRequestOrderItems = orderItemRepository.findAllById(orderItemIds);
        cancelRequestOrderItems.forEach(coi -> coi.updateOrderItemStatus(OrderItemStatus.CANCEL_REQUESTED));
    }

    /*
        itemIds: 반품 상품 id list
    */
    @Override
    public void requestReturn(List<Long> orderItemIds) {
        List<OrderItem> returnRequestOrderItems = orderItemRepository.findAllById(orderItemIds);
        returnRequestOrderItems.forEach(rroi -> rroi.updateOrderItemStatus(OrderItemStatus.RETURN_REQUESTED));
    }

    @Override
    public void updateStatus(List<OrderItem> orderItemList, String status) {
        for (OrderItem orderItem : orderItemList) {
            orderItem.updateOrderItemStatus(OrderItemStatus.valueOf(status));
            if (status.equals(OrderItemStatus.PAID.name())) {
                orderItem.updatePaymentStatus(PaymentStatus.COMPLETED);
            } else if (status.equals(OrderItemStatus.CANCELED.name()) || status.equals(OrderItemStatus.RETURNED.name())) {
                orderItem.updatePaymentStatus(PaymentStatus.CANCELED);
            } else if (status.equals(OrderItemStatus.PAYMENT_FAILED.name())) {
                orderItem.updatePaymentStatus(PaymentStatus.FAILED);
            }
        }
    }

    @Override
    public Long updateStatusByShipment(Long shipmentId, String status) {
        orderItemRepository.updateStatusByShipmentId(OrderItemStatus.valueOf(status), shipmentId);
        OrderItemOrderIdOnly info = orderItemRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER_ITEM));
        return info.getOrderId();
    }

    @Override
    public void assignShipments(List<OrderItem> orderItems, Map<Long, Long> shipmentMap) {
        orderItems.forEach(oi -> oi.assignShipment(shipmentMap.get(oi.getId())));
    }

    @Override
    public Long findOrderIdByOrderItemId(Long orderItemId) {
        OrderItemOrderIdOnly oi = orderItemRepository.findOrderItemOrderIdOnlyById(orderItemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER_ITEM));

        return oi.getOrderId();
    }

    private void validateReservedItem(ItemResponse item) {
        if (item.getIsReserved() && LocalDateTime.now().isBefore(item.getOpenTime())) {
            throw new BusinessException(ExceptionCode.NOT_REACHED_OPEN_TIME);
        }
    }
}
