package com.purchase.preorder.order_service.event.listener.domain;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.listener.AbstractDomainEventListenHelper;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.mapper.OrderKafkaEventMapper;
import com.common.kafka.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDomainEventListenHelper extends AbstractDomainEventListenHelper {

    private final EventFailureService eventFailureService;
    private final KafkaEventPublisher publisher;
    private final OrderKafkaEventMapper mapper;

    @Override
    protected final void saveEventFailure(DomainEvent event, Exception e) {
        eventFailureService.saveEventFailure(event, e);
    }

    protected final void publishWithMapped(DomainEvent event) {
        publisher.publish(mapper.mapFrom(event));
    }
}
