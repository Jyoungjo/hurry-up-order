package com.purchase.preorder.shipment_service.event.publisher;

import com.common.domain.repository.common.OutboxRepository;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.publisher.AbstractDomainEventPublisher;
import com.common.kafka.mapper.OutboxMapper;
import com.common.kafka.mapper.ShipmentKafkaEventMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ShipmentDomainEventPublisher extends AbstractDomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ShipmentKafkaEventMapper mapper;

    public ShipmentDomainEventPublisher(ApplicationEventPublisher eventPublisher,
                                        OutboxRepository outboxRepository,
                                        ShipmentKafkaEventMapper mapper) {
        super(eventPublisher);
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    protected void saveOutboxMessage(DomainEvent event) {
        outboxRepository.save(OutboxMapper.toOutboxMessage(mapper.mapFrom(event)));
    }
}
