package com.common.kafka.mapper;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.order.OrderDeletedDomainEvent;
import com.common.event_common.domain_event_vo.order.OrderPaidDomainEvent;
import com.common.event_common.domain_event_vo.order.OrderPaymentFailedDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByCancelDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByReturnDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByRollbackDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRollbackRequestedDomainEvent;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.event_vo.order.OrderDeletedKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByCancelKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByReturnKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByRollbackKafkaEvent;
import com.common.kafka.event_vo.stock.StockDecrKafkaEvent;
import com.common.kafka.event_vo.stock.StockReservationCanceledKafkaEvent;
import com.common.kafka.event_vo.stock.StockRollbackRequestedKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventMapper {
    private final Map<Class<? extends DomainEvent>, Function<DomainEvent, KafkaEvent>> mappers = Map.of(
            OrderPaidDomainEvent.class, e -> toStockDecrKafkaEvent((OrderPaidDomainEvent) e),
            OrderPaymentFailedDomainEvent.class, e -> toStockReservationCanceledKafkaEvent((OrderPaymentFailedDomainEvent) e),
            PaymentCancelRequestedByCancelDomainEvent.class, e -> toPaymentCancelRequestedByCancelKafkaEvent((PaymentCancelRequestedByCancelDomainEvent) e),
            PaymentCancelRequestedByReturnDomainEvent.class, e -> toPaymentCancelRequestedByReturnKafkaEvent((PaymentCancelRequestedByReturnDomainEvent) e),
            PaymentCancelRequestedByRollbackDomainEvent.class, e -> toPaymentCancelRequestedByRollbackKafkaEvent((PaymentCancelRequestedByRollbackDomainEvent) e),
            OrderDeletedDomainEvent.class, e -> toOrderDeletedKafkaEvent((OrderDeletedDomainEvent) e),
            StockRollbackRequestedDomainEvent.class, e -> toStockRollbackRequestedKafkaEvent((StockRollbackRequestedDomainEvent) e)
    );

    public KafkaEvent mapFrom(DomainEvent event) {
        Function<DomainEvent, KafkaEvent> mapper = mappers.get(event.getClass());
        if (mapper == null) throw new IllegalArgumentException("지원하지 않는 이벤트: " + event.getClass());
        return mapper.apply(event);
    }

    public KafkaEvent toStockDecrKafkaEvent(OrderPaidDomainEvent domainEvent) {
        return StockDecrKafkaEvent.builder()
                .topic(TopicNames.STOCK_DECR)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .userId(domainEvent.getUserId())
                .itemIds(domainEvent.getItemIds())
                .eventType(KafkaEventType.STOCK_DECREASE_REQ)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toStockReservationCanceledKafkaEvent(OrderPaymentFailedDomainEvent domainEvent) {
        return StockReservationCanceledKafkaEvent.builder()
                .topic(TopicNames.STOCK_RESERVATION_CANCELED)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .userId(domainEvent.getUserId())
                .itemIds(domainEvent.getItemIds())
                .eventType(KafkaEventType.STOCK_RESERVATION_CANCELED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCancelRequestedByCancelKafkaEvent(PaymentCancelRequestedByCancelDomainEvent domainEvent) {
        return PaymentCancelRequestedByCancelKafkaEvent.builder()
                .topic(TopicNames.PAYMENT_CANCEL_REQUESTED_BY_CANCEL)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .cancelReason(domainEvent.getCancelReason())
                .eventType(KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_CANCEL)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCancelRequestedByReturnKafkaEvent(PaymentCancelRequestedByReturnDomainEvent domainEvent) {
        return PaymentCancelRequestedByReturnKafkaEvent.builder()
                .topic(TopicNames.PAYMENT_CANCEL_REQUESTED_BY_RETURN)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .shipmentId(domainEvent.getShipmentId())
                .cancelReason(domainEvent.getCancelReason())
                .eventType(KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_RETURN)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCancelRequestedByRollbackKafkaEvent(PaymentCancelRequestedByRollbackDomainEvent domainEvent) {
        return PaymentCancelRequestedByRollbackKafkaEvent.builder()
                .topic(TopicNames.PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .cancelReason(domainEvent.getCancelReason())
                .eventType(KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toOrderDeletedKafkaEvent(OrderDeletedDomainEvent domainEvent) {
        return OrderDeletedKafkaEvent.builder()
                .topic(TopicNames.ORDER_DELETED)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .eventType(KafkaEventType.ORDER_DELETED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toStockRollbackRequestedKafkaEvent(StockRollbackRequestedDomainEvent domainEvent) {
        return StockRollbackRequestedKafkaEvent.builder()
                .topic(TopicNames.STOCK_ROLLBACK_REQUESTED)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .stockMap(domainEvent.getStockMap())
                .eventType(KafkaEventType.STOCK_ROLLBACK_REQUESTED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }
}
