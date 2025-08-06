package com.common.event_common.domain_event_vo;

import java.time.LocalDateTime;

public interface DomainEvent {
    String getDomainEventType();
    String getAggregateId();
    LocalDateTime getOccurredAt();
    String getEventId();
}
