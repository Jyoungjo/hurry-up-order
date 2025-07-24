package com.common.event_common.handler;

import com.common.domain.entity.common.EventFailure;

public interface EventFailureHandler<T> {
    void saveFailure(T event, Throwable ex, EventFailure.EventFailureCategory category);
}
