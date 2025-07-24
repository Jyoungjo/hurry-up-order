package com.purchase.preorder.stock_service.event.publisher.kafka;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.outbox.OutboxService;
import com.common.kafka.publisher.AbstractKafkaEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class StockKafkaEventPublisher extends AbstractKafkaEventPublisher {

    private final OutboxService outboxService;

    public StockKafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Qualifier("businessEventTaskExecutor") Executor executor,
                                    OutboxService outboxService) {
        super(kafkaTemplate, executor);
        this.outboxService = outboxService;
    }

    @Override
    protected void updatePublished(KafkaEvent kafkaEvent) {
        outboxService.markPublished(kafkaEvent);
    }

    @Override
    public void fail(OutboxMessage outboxMessage) {
        outboxService.markAsFailure(outboxMessage);
    }
}
