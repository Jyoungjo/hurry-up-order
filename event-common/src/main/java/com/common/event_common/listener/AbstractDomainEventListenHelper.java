package com.common.event_common.listener;

import com.common.event_common.domain_event_vo.DomainEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDomainEventListenHelper {

    public final void executeWithFailureHandling(DomainEvent event, Runnable logic) {
        try {
            logic.run();
            log.info("[{}] 처리 완료", event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[{}] 처리 실패 - 원인: {}", event.getClass().getSimpleName(), e.getMessage());
            saveEventFailure(event, e);
        }
    }

    public final void executeOnlyPublishKafkaEvent(DomainEvent event) {
        publishWithMapped(event);
    }

    protected void publishWithMapped(DomainEvent event) {}
    protected void saveEventFailure(DomainEvent event, Exception e) {}
}
