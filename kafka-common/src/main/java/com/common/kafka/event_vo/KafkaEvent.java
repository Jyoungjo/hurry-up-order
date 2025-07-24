package com.common.kafka.event_vo;

import java.time.LocalDateTime;

public interface KafkaEvent {
    String getTopic();
    String getAggregateId();
    String getEventType();
    String getEventId();
    LocalDateTime getOccurredAt();
}
