package com.common.kafka.outbox;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractOutboxService implements OutboxService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublished(KafkaEvent kafkaEvent) {
        OutboxMessage outbox = getOutBox(kafkaEvent);
        outbox.markPublished();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailure(OutboxMessage outbox) {
        outbox.increaseRetryCount();
        save(outbox);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboxMessage> loadUnpublished() {
        return getTop100ByUnpublishedList();
    }

    protected abstract OutboxMessage getOutBox(KafkaEvent kafkaEvent);
    protected abstract void save(OutboxMessage outbox);
    protected abstract List<OutboxMessage> getTop100ByUnpublishedList();
}
