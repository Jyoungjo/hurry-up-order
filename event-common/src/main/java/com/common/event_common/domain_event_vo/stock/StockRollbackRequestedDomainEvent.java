package com.common.event_common.domain_event_vo.stock;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.DomainEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRollbackRequestedDomainEvent implements DomainEvent {

    private String eventId;
    private Long orderId;
    private Map<Long, Integer> stockMap;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.STOCK_ROLLBACK_REQUESTED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.orderId);
    }
}
