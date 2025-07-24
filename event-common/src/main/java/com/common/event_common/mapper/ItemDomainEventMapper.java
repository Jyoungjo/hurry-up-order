package com.common.event_common.mapper;

import com.common.event_common.domain_event_vo.item.ItemCreatedDomainEvent;
import com.common.event_common.domain_event_vo.item.ItemDeletedDomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ItemDomainEventMapper {

    public ItemCreatedDomainEvent toItemCreatedEvent(Long itemId, int initQuantity) {
        return ItemCreatedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .itemId(itemId)
                .initQuantity(initQuantity)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public ItemDeletedDomainEvent toItemDeletedEvent(Long itemId) {
        return ItemDeletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .itemId(itemId)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
