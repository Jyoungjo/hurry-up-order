package com.purchase.preorder.item_service.event.publisher.domain;

import com.common.domain.repository.common.OutboxRepository;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.publisher.AbstractDomainEventPublisher;
import com.common.kafka.mapper.ItemKafkaEventMapper;
import com.common.kafka.mapper.OutboxMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ItemDomainEventPublisher extends AbstractDomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ItemKafkaEventMapper mapper;

    public ItemDomainEventPublisher(ApplicationEventPublisher eventPublisher,
                                    OutboxRepository outboxRepository,
                                    ItemKafkaEventMapper mapper) {
        super(eventPublisher);
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    protected void saveOutboxMessage(DomainEvent event) {
        outboxRepository.save(OutboxMapper.toOutboxMessage(mapper.mapFrom(event)));
    }
}
