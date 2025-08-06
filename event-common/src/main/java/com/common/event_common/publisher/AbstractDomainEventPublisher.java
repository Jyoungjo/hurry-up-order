package com.common.event_common.publisher;

import com.common.event_common.domain_event_vo.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    protected abstract void saveOutboxMessage(DomainEvent event);

    /*
     * Kafka 대상 이벤트 → Outbox 저장 + Spring Event 발행
     */
    @Override
    public void publishWithOutboxAfterCommit(DomainEvent event) {
        log.info("Spring Event + Kafka 이벤트 발행 시작 - 이벤트 대상: {}", event.getDomainEventType());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                saveOutboxMessage(event);
            }

            @Override
            public void afterCommit() {
                eventPublisher.publishEvent(event);
            }
        });
        log.info("이벤트 발행 완료");
    }

    /*
     * 내부 처리용 Spring Event만 발행
     */
    @Override
    public void publishOnlySpringEventAfterCommit(DomainEvent event) {
        log.info("Spring Event 이벤트 발행 시작 - 이벤트 대상: {}", event.getDomainEventType());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publishEvent(event);
            }
        });
        log.info("이벤트 발행 완료");
    }
}
