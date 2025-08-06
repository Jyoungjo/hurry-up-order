package com.common.kafka.publisher;

import com.common.kafka.event_vo.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaEventPublisher implements KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Executor executor;

    protected abstract void updatePublished(KafkaEvent kafkaEvent);

    @Override
    public void publish(KafkaEvent kafkaEvent) {
        try {
            kafkaTemplate.send(kafkaEvent.getTopic(), kafkaEvent.getAggregateId(), kafkaEvent).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("이벤트 발행 완료 - 이벤트: {} -> Outbox 업데이트", kafkaEvent.getClass().getSimpleName());
                    CompletableFuture
                            .runAsync(() -> updatePublished(kafkaEvent), executor)
                            .exceptionally(exception -> {
                                log.error("Outbox 업데이트 실패 - 원인: {}", exception.getMessage());
                                return null;
                            });
                } else throw new RuntimeException(ex);
            });
        } catch (Exception e) {
            log.warn("이벤트 발행 실패 - 이벤트: {} 원인: {}", kafkaEvent.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
