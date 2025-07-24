package com.purchase.preorder.payment_service_common.event_fail;

import com.common.domain.entity.common.EventFailure;
import com.common.domain.repository.common.EventFailureRepository;
import com.common.event_common.handler.DomainEventFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDomainEventFailureHandler extends DomainEventFailureHandler {

    private final EventFailureRepository repository;

    @Override
    protected final void doSave(EventFailure eventFailure) {
        repository.save(eventFailure);
    }
}
