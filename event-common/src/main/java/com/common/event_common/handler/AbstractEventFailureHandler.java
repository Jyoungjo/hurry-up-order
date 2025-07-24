package com.common.event_common.handler;

import com.common.domain.entity.common.EventFailure;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractEventFailureHandler<T> implements EventFailureHandler<T> {

    @Override
    @Transactional
    public void saveFailure(T event, Throwable ex, EventFailure.EventFailureCategory category) {
        EventFailure failure = EventFailure.of(
                getEventType(event),
                serializeEvent(event),
                ex.getMessage(),
                category
        );
        doSave(failure);
    }

    protected abstract String getEventType(T event);
    protected abstract String serializeEvent(T event);
    protected abstract void doSave(EventFailure eventFailure);
}
