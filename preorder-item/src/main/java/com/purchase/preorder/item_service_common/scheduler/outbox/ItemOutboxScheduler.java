package com.purchase.preorder.item_service_common.scheduler.outbox;

import com.common.kafka.outbox.AbstractOutboxScheduler;
import com.common.kafka.outbox.OutboxService;
import com.common.kafka.outbox.OutboxServiceNames;
import com.common.kafka.publisher.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemOutboxScheduler extends AbstractOutboxScheduler {

    public ItemOutboxScheduler(OutboxService outboxService,
                               KafkaEventPublisher publisher,
                               ObjectMapper objectMapper) {
        super(outboxService, publisher, objectMapper);
    }

    @Override
    protected String getServiceName() {
        return OutboxServiceNames.ITEM_SERVICE.getServiceName();
    }
}
