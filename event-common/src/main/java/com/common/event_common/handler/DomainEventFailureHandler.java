package com.common.event_common.handler;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.web.util.EventSerializer;

public abstract class DomainEventFailureHandler extends AbstractEventFailureHandler<DomainEvent> {

    @Override
    protected final String getEventType(DomainEvent event) {
        return event.getDomainEventType();
    }

    @Override
    protected final String serializeEvent(DomainEvent event) {
        return EventSerializer.serializeEvent(event);
    }
}
