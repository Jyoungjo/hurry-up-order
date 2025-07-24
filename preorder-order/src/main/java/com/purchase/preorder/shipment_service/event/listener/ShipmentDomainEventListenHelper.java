package com.purchase.preorder.shipment_service.event.listener;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.listener.AbstractDomainEventListenHelper;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.mapper.ShipmentKafkaEventMapper;
import com.common.kafka.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentDomainEventListenHelper extends AbstractDomainEventListenHelper {

    private final EventFailureService eventFailureService;
    private final KafkaEventPublisher publisher;
    private final ShipmentKafkaEventMapper mapper;

    @Override
    protected final void saveEventFailure(DomainEvent event, Exception e) {
        eventFailureService.saveEventFailure(event, e);
    }

    protected final void publishWithMapped(DomainEvent event) {
        publisher.publish(mapper.mapFrom(event));
    }
}
