package com.purchase.preorder.user_service_common.scheduler.retry.handler;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.user.UserCreatedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.email_service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedDomainEventRetryHandler implements EventRetryHandler<UserCreatedDomainEvent> {

    private final EmailService emailService;

    @Override
    public String getEventType() {
        return DomainEventType.USER_CREATED.name();
    }

    @Override
    public void handle(UserCreatedDomainEvent event) throws Exception {
        emailService.send(event.getEmail());
    }

    @Override
    public Class<UserCreatedDomainEvent> getEventClass() {
        return UserCreatedDomainEvent.class;
    }
}
