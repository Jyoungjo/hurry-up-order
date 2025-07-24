package com.purchase.preorder.order_service_common.scheduler.retry;

import com.common.domain.entity.common.EventFailure;
import com.common.domain.repository.common.EventFailureRepository;
import com.common.event_common.handler.EventRetryHandlerRegistry;
import com.common.event_common.retry.AbstractEventRetryHelper;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderEventRetryHelper extends AbstractEventRetryHelper {

    private final EventFailureRepository eventFailureRepository;

    public OrderEventRetryHelper(EventRetryHandlerRegistry eventRetryHandlerRegistry,
                                 EventFailureRepository eventFailureRepository) {
        super(eventRetryHandlerRegistry);
        this.eventFailureRepository = eventFailureRepository;
    }

    @Override
    protected void doSave(@NonNull EventFailure failure) {
        eventFailureRepository.save(failure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFailure> loadFailures() {
        return eventFailureRepository.findTop100ByProcessedFalseAndRetryCountLessThan(5);
    }
}

