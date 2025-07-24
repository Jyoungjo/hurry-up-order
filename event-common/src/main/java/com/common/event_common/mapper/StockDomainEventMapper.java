package com.common.event_common.mapper;

import com.common.event_common.domain_event_vo.stock.StockCreatedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockDecreasedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRollbackRequestedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRolledBackDomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class StockDomainEventMapper {

    public StockCreatedDomainEvent toStockCreatedEvent(Long itemId, Long stockId) {
        return StockCreatedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .itemId(itemId)
                .stockId(stockId)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public StockDecreasedDomainEvent toStockDecreasedEvent(Long orderId) {
        return StockDecreasedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public StockRedisRollbackRequestedDomainEvent toStockRedisRollbackEvent(Long orderId, Map<Long, Integer> qtyMap) {
        return StockRedisRollbackRequestedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .qtyMap(qtyMap)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public StockRedisRolledBackDomainEvent toStockRedisRolledBackEvent(Long orderId) {
        return StockRedisRolledBackDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
