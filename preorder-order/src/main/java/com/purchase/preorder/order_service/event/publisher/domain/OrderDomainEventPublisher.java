package com.purchase.preorder.order_service.event.publisher.domain;

import com.common.domain.repository.common.OutboxRepository;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.publisher.AbstractDomainEventPublisher;
import com.common.kafka.mapper.OrderKafkaEventMapper;
import com.common.kafka.mapper.OutboxMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderDomainEventPublisher extends AbstractDomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final OrderKafkaEventMapper mapper;

    public OrderDomainEventPublisher(ApplicationEventPublisher eventPublisher,
                                     OutboxRepository outboxRepository,
                                     OrderKafkaEventMapper mapper) {
        super(eventPublisher);
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    protected void saveOutboxMessage(DomainEvent event) {
        outboxRepository.save(OutboxMapper.toOutboxMessage(mapper.mapFrom(event)));
    }
}
