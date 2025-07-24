package com.common.kafka.publisher;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;

public interface KafkaEventPublisher {
    void publish(KafkaEvent kafkaEvent);
    void fail(OutboxMessage outboxMessage);
}
