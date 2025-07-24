package com.common.kafka.mapper;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.stock.StockDecreasedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRolledBackDomainEvent;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.event_vo.stock.StockDecreasedKafkaEvent;
import com.common.kafka.event_vo.stock.StockRedisRolledBackKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockKafkaEventMapper {
    private final Map<Class<? extends DomainEvent>, Function<DomainEvent, KafkaEvent>> mappers = Map.of(
            StockDecreasedDomainEvent.class, e -> toStockDecreasedKafkaEvent((StockDecreasedDomainEvent) e),
            StockRedisRolledBackDomainEvent.class, e -> toStockRedisRolledbackKafkaEvent((StockRedisRolledBackDomainEvent) e)
    );

    public KafkaEvent mapFrom(DomainEvent event) {
        Function<DomainEvent, KafkaEvent> mapper = mappers.get(event.getClass());
        if (mapper == null) throw new IllegalArgumentException("지원하지 않는 이벤트: " + event.getClass());
        return mapper.apply(event);
    }

    public KafkaEvent toStockDecreasedKafkaEvent(StockDecreasedDomainEvent domainEvent) {
        return StockDecreasedKafkaEvent.builder()
                .topic(TopicNames.STOCK_DECREASED)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .eventType(KafkaEventType.STOCK_DECREASED)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }

    public KafkaEvent toStockRedisRolledbackKafkaEvent(StockRedisRolledBackDomainEvent domainEvent) {
        return StockRedisRolledBackKafkaEvent.builder()
                .topic(TopicNames.STOCK_REDIS_ROLLED_BACK)
                .aggregateId(domainEvent.getAggregateId())
                .eventId(domainEvent.getEventId())
                .orderId(domainEvent.getOrderId())
                .eventType(KafkaEventType.STOCK_REDIS_ROLLED_BACK)
                .occurredAt(domainEvent.getOccurredAt())
                .build();
    }
}
