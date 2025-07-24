package com.common.kafka.mapper;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.kafka.event_vo.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentKafkaEventMapper {
    private final Map<Class<? extends DomainEvent>, Function<DomainEvent, KafkaEvent>> mappers = Map.of();

    public KafkaEvent mapFrom(DomainEvent event) {
        Function<DomainEvent, KafkaEvent> mapper = mappers.get(event.getClass());
        if (mapper == null) throw new IllegalArgumentException("지원하지 않는 이벤트: " + event.getClass());
        return mapper.apply(event);
    }
}
