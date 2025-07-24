package com.common.kafka.event_vo.order;

import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.KafkaEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName(KafkaEventType.ORDER_DELETED)
public class OrderDeletedKafkaEvent implements KafkaEvent {

    private String topic;
    private String aggregateId;
    private String eventId;
    private String eventType;
    private Long orderId;
    private LocalDateTime occurredAt;
}
