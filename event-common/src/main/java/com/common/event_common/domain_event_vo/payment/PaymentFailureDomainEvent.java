package com.common.event_common.domain_event_vo.payment;

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
public class PaymentFailureDomainEvent implements DomainEvent {

    private String eventId;
    private Long orderId;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.PAYMENT_FAILURE.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(orderId);
    }
}
