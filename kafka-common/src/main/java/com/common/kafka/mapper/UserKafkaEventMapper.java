package com.common.kafka.mapper;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.event_vo.user.UserDeletedKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaEventMapper {
    private final Map<Class<? extends DomainEvent>, Function<DomainEvent, KafkaEvent>> mappers = Map.of(
            UserDeletedDomainEvent.class, e -> toUserDeletedKafkaEvent((UserDeletedDomainEvent) e)
    );

    public KafkaEvent mapFrom(DomainEvent event) {
        Function<DomainEvent, KafkaEvent> mapper = mappers.get(event.getClass());
        if (mapper == null) throw new IllegalArgumentException("지원하지 않는 이벤트: " + event.getClass());
        return mapper.apply(event);
    }

    public KafkaEvent toUserDeletedKafkaEvent(UserDeletedDomainEvent domainEvent) {
        return UserDeletedKafkaEvent.builder()
                .topic(TopicNames.USER_DELETED)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .userId(domainEvent.getUserId())
                .eventType(KafkaEventType.USER_DELETED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }
}
