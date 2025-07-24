package com.common.event_common.domain_event_vo.stock;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.DomainEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCreatedDomainEvent implements DomainEvent {

    private String eventId;
    private Long itemId;
    private Long stockId;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.STOCK_CREATED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.itemId);
    }
}
