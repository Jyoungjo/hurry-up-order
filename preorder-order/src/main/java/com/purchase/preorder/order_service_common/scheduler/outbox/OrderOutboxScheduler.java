package com.purchase.preorder.order_service_common.scheduler.outbox;

import com.common.kafka.outbox.AbstractOutboxScheduler;
import com.common.kafka.outbox.OutboxService;
import com.common.kafka.outbox.OutboxServiceNames;
import com.common.kafka.publisher.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxScheduler extends AbstractOutboxScheduler {

    public OrderOutboxScheduler(OutboxService outboxService,
                                KafkaEventPublisher publisher,
                                ObjectMapper objectMapper) {
        super(outboxService, publisher, objectMapper);
    }

    @Override
    protected String getServiceName() {
        return OutboxServiceNames.ORDER_SERVICE.getServiceName();
    }
}
