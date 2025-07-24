package com.common.event_common.mapper;

import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.OrderItem;
import com.common.domain.entity.order.projection.OrderItemPaidInfo;
import com.common.domain.entity.order.projection.OrderPaidInfo;
import com.common.event_common.domain_event_vo.order.*;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByCancelDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByReturnDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByRollbackDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRollbackRequestedDomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderDomainEventMapper {

    public OrderPaidDomainEvent toOrderPaidEvent(OrderPaidInfo info) {
        return OrderPaidDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(info.getId())
                .userId(info.getUserId())
                .itemIds(info.getOrderItemList().stream().map(OrderItemPaidInfo::getItemId).toList())
                .orderItemIds(info.getOrderItemList().stream().map(OrderItemPaidInfo::getId).toList())
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderCompletedDomainEvent toOrderCompletedEvent(Long orderId, List<Long> orderItemIds) {
        return OrderCompletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderItemIds(orderItemIds)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderPaymentFailedDomainEvent toOrderPaymentFailedEvent(Order order) {
        return OrderPaymentFailedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .userId(order.getUserId())
                .itemIds(order.getOrderItemList().stream().map(OrderItem::getItemId).toList())
                .orderItemIds(order.getOrderItemList().stream().map(OrderItem::getId).toList())
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderCancelRequestedDomainEvent toOrderCancelRequestedEvent(Long orderId, List<Long> orderItemIds, String cancelReason) {
        return OrderCancelRequestedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderItemIds(orderItemIds)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCancelRequestedByCancelDomainEvent toPaymentCancelRequestedByCancelEvent(Long orderId, String cancelReason) {
        return PaymentCancelRequestedByCancelDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCancelRequestedByReturnDomainEvent toPaymentCancelRequestedByReturnEvent(Long shipmentId, Long orderId, String cancelReason) {
        return PaymentCancelRequestedByReturnDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCancelRequestedByRollbackDomainEvent toPaymentCancelRequestedByRollbackEvent(Long orderId, String cancelReason) {
        return PaymentCancelRequestedByRollbackDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderReturnRequestedDomainEvent toOrderReturnRequestedEvent(Long orderId, List<Long> orderItemIds) {
        return OrderReturnRequestedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderItemIds(orderItemIds)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderDeletedDomainEvent toOrderDeletedEvent(Long userid, Long orderId, List<Long> orderItemIds) {
        return OrderDeletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userid)
                .orderId(orderId)
                .orderItemIds(orderItemIds)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public StockRollbackRequestedDomainEvent toStockRollbackRequestedEvent(Long orderId, Map<Long, Integer> stockMap) {
        return StockRollbackRequestedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .stockMap(stockMap)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public OrderCompensationCompletedDomainEvent toOrderCompensationCompletedEvent(Long orderId, List<Long> orderItemIds) {
        return OrderCompensationCompletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderItemIds(orderItemIds)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
