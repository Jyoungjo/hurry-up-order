package com.common.event_common.handler;

public interface EventRetryHandler<T> {
    String getEventType();
    void handle(T event) throws Exception;
    Class<T> getEventClass();
}
