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
public class OrderCancelRequestedDomainEvent implements DomainEvent {
    private String eventId;
    private Long orderId;
    private List<Long> orderItemIds;
    private String cancelReason;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.ORDER_CANCEL_REQUESTED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.orderId);
    }
}
