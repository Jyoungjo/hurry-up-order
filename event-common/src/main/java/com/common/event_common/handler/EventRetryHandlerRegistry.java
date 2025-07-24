package com.common.event_common.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventRetryHandlerRegistry {

    private final Map<String, EventRetryHandler<?>> handlerMap = new HashMap<>();

    public EventRetryHandlerRegistry(List<EventRetryHandler<?>> handlers) {
        for (EventRetryHandler<?> handler : handlers) {
            handlerMap.put(handler.getEventType(), handler);
        }
    }

    public EventRetryHandler<?> getHandler(String eventType) {
        EventRetryHandler<?> handler = handlerMap.get(eventType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown eventType: " + eventType);
        }
        return handler;
    }
}
