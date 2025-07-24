package com.common.event_common.publisher;

import com.common.event_common.domain_event_vo.DomainEvent;

public interface DomainEventPublisher {
    void publishWithOutboxAfterCommit(DomainEvent event);
    void publishOnlySpringEventAfterCommit(DomainEvent event);
}
