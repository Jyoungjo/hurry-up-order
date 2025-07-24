package com.common.event_common.retry;

import com.common.domain.entity.common.EventFailure;
import com.common.event_common.handler.EventRetryHandler;
import com.common.event_common.handler.EventRetryHandlerRegistry;
import com.common.web.util.ObjectMapperProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractEventRetryHelper implements EventRetryHelper {

    private final EventRetryHandlerRegistry eventRetryHandlerRegistry;

    public abstract List<EventFailure> loadFailures();
    protected abstract void doSave(@NonNull EventFailure failure);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(EventFailure failure, Exception e) {
        failure.fail(e.getMessage());
        doSave(failure);
    }

    @Override
    @Transactional
    public void processEvent(EventFailure failure) throws Exception {
        EventRetryHandler<?> handler = eventRetryHandlerRegistry.getHandler(failure.getEventType());
        Object event = deserialize(failure, handler.getEventClass());
        handle(handler, event);
        failure.success();
    }

    protected Object deserialize(@NonNull EventFailure failure, @NonNull Class<?> clazz) throws Exception {
        return ObjectMapperProvider.get().readValue(failure.getPayload(), clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> void handle(EventRetryHandler<T> handler, Object event) throws Exception {
        handler.handle((T) event);
    }
}
