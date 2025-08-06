package com.common.kafka.outbox;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;

import java.util.List;

public interface OutboxService {
    void markPublished(KafkaEvent kafkaEvent);
    void markAsFailure(OutboxMessage outbox);
    List<OutboxMessage> loadUnpublished();
}
