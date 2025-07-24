package com.common.event_common.domain_event_vo.shipment;

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
public class ShipmentReturnedDomainEvent implements DomainEvent {

    private String eventId;
    private Long shipmentId;
    private Long orderItemId;
    private String cancelReason;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.SHIPMENT_RETURNED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.shipmentId);
    }
}
