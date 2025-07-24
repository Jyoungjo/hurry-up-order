package com.common.kafka.event_vo.user;

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
public class UserDeletedKafkaEvent implements KafkaEvent {
    private String topic;
    private String aggregateId;
    private String eventId;
    private String eventType;
    private LocalDateTime occurredAt;
    private Long userId;
}
