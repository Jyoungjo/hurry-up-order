package com.purchase.preorder.user_service.event.listener.domain;

import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDomainEventListener {

    private final UserDomainEventListenHelper helper;

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(UserDeletedDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }
}
