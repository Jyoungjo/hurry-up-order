package com.common.kafka.outbox;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.publisher.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractOutboxScheduler {

    private final OutboxService outboxService;
    private final KafkaEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 30000) // 30초마다
    public void retryOutbox() {
        List<OutboxMessage> outboxMessages = outboxService.loadUnpublished();

        for (OutboxMessage outboxMessage : outboxMessages) {
            try {
                log.info("[{}] Outbox 이벤트 RETRY 시도", getServiceName());
                publisher.publish(objectMapper.readValue(outboxMessage.getPayload(), KafkaEvent.class));
            } catch (Exception e) {
                log.warn("[{}] Outbox 이벤트 RETRY 실패 - 원인: {}", getServiceName(), e.getMessage());
                publisher.fail(outboxMessage);
            }
        }
    }

    protected abstract String getServiceName();
}
