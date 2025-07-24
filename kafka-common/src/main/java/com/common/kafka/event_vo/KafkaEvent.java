package com.common.kafka.event_vo;

import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.order.OrderDeletedKafkaEvent;
import com.common.kafka.event_vo.payment.*;
import com.common.kafka.event_vo.stock.*;
import com.common.kafka.event_vo.user.UserDeletedKafkaEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserDeletedKafkaEvent.class, name = KafkaEventType.USER_DELETED),
        @JsonSubTypes.Type(value = OrderDeletedKafkaEvent.class, name = KafkaEventType.ORDER_DELETED),
        @JsonSubTypes.Type(value = PaymentSucceedKafkaEvent.class, name = KafkaEventType.PAYMENT_SUCCEED),
        @JsonSubTypes.Type(value = PaymentFailureKafkaEvent.class, name = KafkaEventType.PAYMENT_FAILURE),
        @JsonSubTypes.Type(value = PaymentCancelRequestedByCancelKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_CANCEL),
        @JsonSubTypes.Type(value = PaymentCancelRequestedByReturnKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_RETURN),
        @JsonSubTypes.Type(value = PaymentCancelRequestedByRollbackKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK),
        @JsonSubTypes.Type(value = PaymentCanceledByCancelKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCELED_BY_CANCEL),
        @JsonSubTypes.Type(value = PaymentCanceledByReturnKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCELED_BY_RETURN),
        @JsonSubTypes.Type(value = PaymentCanceledByRollbackKafkaEvent.class, name = KafkaEventType.PAYMENT_CANCELED_BY_ROLLBACK),
        @JsonSubTypes.Type(value = StockDecrKafkaEvent.class, name = KafkaEventType.STOCK_DECREASE_REQ),
        @JsonSubTypes.Type(value = StockReservationCanceledKafkaEvent.class, name = KafkaEventType.STOCK_RESERVATION_CANCELED),
        @JsonSubTypes.Type(value = StockDecreasedKafkaEvent.class, name = KafkaEventType.STOCK_DECREASED),
        @JsonSubTypes.Type(value = StockRedisRolledBackKafkaEvent.class, name = KafkaEventType.STOCK_REDIS_ROLLED_BACK),
        @JsonSubTypes.Type(value = StockRollbackRequestedKafkaEvent.class, name = KafkaEventType.STOCK_ROLLBACK_REQUESTED),
})
public interface KafkaEvent {
    String getTopic();
    String getAggregateId();
    String getEventType();
    String getEventId();
    LocalDateTime getOccurredAt();
}
