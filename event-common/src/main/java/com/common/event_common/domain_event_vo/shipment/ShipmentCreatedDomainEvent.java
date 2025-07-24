package com.common.event_common.domain_event_vo.shipment;

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
public class ShipmentCreatedDomainEvent implements DomainEvent {

    private String eventId;
    private Long orderId;
    private Map<Long, Long> shipmentMap;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.SHIPMENT_CREATED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.orderId);
    }
}
