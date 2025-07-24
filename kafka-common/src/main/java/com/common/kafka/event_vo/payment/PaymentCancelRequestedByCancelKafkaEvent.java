package com.common.kafka.event_vo.payment;

import com.common.kafka.event_vo.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelRequestedByCancelKafkaEvent implements KafkaEvent {

    private String topic;
    private String aggregateId;
    private String eventId;
    private String eventType;
    private Long orderId;
    private String cancelReason;
    private LocalDateTime occurredAt;
}
