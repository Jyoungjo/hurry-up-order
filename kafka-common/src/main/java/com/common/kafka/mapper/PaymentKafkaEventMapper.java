package com.common.kafka.mapper;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.payment.*;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.event_vo.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventMapper {
    private final Map<Class<? extends DomainEvent>, Function<DomainEvent, KafkaEvent>> mappers = Map.of(
            PaymentSucceedDomainEvent.class, e -> toPaymentSucceedKafkaEvent((PaymentSucceedDomainEvent) e),
            PaymentFailureDomainEvent.class, e -> toPaymentFailureKafkaEvent((PaymentFailureDomainEvent) e),
            PaymentCanceledByCancelDomainEvent.class, e -> toPaymentCanceledByCancelKafkaEvent((PaymentCanceledByCancelDomainEvent) e),
            PaymentCanceledByReturnDomainEvent.class, e -> toPaymentCanceledByReturnKafkaEvent((PaymentCanceledByReturnDomainEvent) e),
            PaymentCanceledByRollbackDomainEvent.class, e -> toPaymentCanceledByRollbackKafkaEvent((PaymentCanceledByRollbackDomainEvent) e)
    );

    public KafkaEvent mapFrom(DomainEvent event) {
        Function<DomainEvent, KafkaEvent> mapper = mappers.get(event.getClass());
        if (mapper == null) throw new IllegalArgumentException("지원하지 않는 이벤트: " + event.getClass());
        return mapper.apply(event);
    }

    public KafkaEvent toPaymentSucceedKafkaEvent(PaymentSucceedDomainEvent domainEvent) {
        return PaymentSucceedKafkaEvent.builder()
                .topic(TopicNames.ORDER_EVENTS)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .eventType(KafkaEventType.PAYMENT_SUCCEED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentFailureKafkaEvent(PaymentFailureDomainEvent domainEvent) {
        return PaymentFailureKafkaEvent.builder()
                .topic(TopicNames.ORDER_EVENTS)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .eventType(KafkaEventType.PAYMENT_FAILURE)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCanceledByCancelKafkaEvent(PaymentCanceledByCancelDomainEvent domainEvent) {
        return PaymentCanceledByCancelKafkaEvent.builder()
                .topic(TopicNames.ORDER_EVENTS)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .cancelReason(CancelReason.from(domainEvent.getCancelReason()))
                .eventType(KafkaEventType.PAYMENT_CANCELED_BY_CANCEL)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCanceledByReturnKafkaEvent(PaymentCanceledByReturnDomainEvent domainEvent) {
        return PaymentCanceledByReturnKafkaEvent.builder()
                .topic(TopicNames.ORDER_EVENTS)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .shipmentId(domainEvent.getShipmentId())
                .cancelReason(CancelReason.from(domainEvent.getCancelReason()))
                .eventType(KafkaEventType.PAYMENT_CANCELED_BY_RETURN)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toPaymentCanceledByRollbackKafkaEvent(PaymentCanceledByRollbackDomainEvent domainEvent) {
        return PaymentCanceledByCancelKafkaEvent.builder()
                .topic(TopicNames.ORDER_EVENTS)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .cancelReason(CancelReason.from(domainEvent.getCancelReason()))
                .eventType(KafkaEventType.PAYMENT_CANCELED_BY_ROLLBACK)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }
}
