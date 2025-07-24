package com.purchase.preorder.email_service.email.event.listener;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.listener.AbstractDomainEventListenHelper;
import com.common.kafka.fail.EventFailureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailDomainEventListenHelper extends AbstractDomainEventListenHelper {

    private final EventFailureService eventFailureService;

    @Override
    protected final void saveEventFailure(DomainEvent event, Exception e) {
        eventFailureService.saveEventFailure(event, e);
    }
}
