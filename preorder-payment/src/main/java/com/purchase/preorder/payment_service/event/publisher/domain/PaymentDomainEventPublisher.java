package com.purchase.preorder.payment_service.event.publisher.domain;

import com.common.domain.repository.common.OutboxRepository;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.publisher.AbstractDomainEventPublisher;
import com.common.kafka.mapper.OutboxMapper;
import com.common.kafka.mapper.PaymentKafkaEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentDomainEventPublisher extends AbstractDomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final PaymentKafkaEventMapper mapper;

    public PaymentDomainEventPublisher(ApplicationEventPublisher eventPublisher,
                                       OutboxRepository outboxRepository,
                                       PaymentKafkaEventMapper mapper) {
        super(eventPublisher);
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    protected void saveOutboxMessage(DomainEvent event) {
        outboxRepository.save(OutboxMapper.toOutboxMessage(mapper.mapFrom(event)));
    }
}
