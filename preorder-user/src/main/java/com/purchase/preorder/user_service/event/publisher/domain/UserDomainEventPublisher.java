package com.purchase.preorder.user_service.event.publisher.domain;

import com.common.domain.repository.common.OutboxRepository;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.publisher.AbstractDomainEventPublisher;
import com.common.kafka.mapper.OutboxMapper;
import com.common.kafka.mapper.UserKafkaEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDomainEventPublisher extends AbstractDomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final UserKafkaEventMapper mapper;

    public UserDomainEventPublisher(ApplicationEventPublisher eventPublisher,
                                    OutboxRepository outboxRepository,
                                    UserKafkaEventMapper mapper) {
        super(eventPublisher);
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    protected void saveOutboxMessage(DomainEvent event) {
        outboxRepository.save(OutboxMapper.toOutboxMessage(mapper.mapFrom(event)));
    }
}
