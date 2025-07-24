package com.common.event_common.domain_event_vo.order;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.DomainEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDeletedDomainEvent implements DomainEvent {
    private String eventId;
    private Long userId;
    private Long orderId;
    private List<Long> orderItemIds;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.ORDER_DELETED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.orderId);
    }
}
