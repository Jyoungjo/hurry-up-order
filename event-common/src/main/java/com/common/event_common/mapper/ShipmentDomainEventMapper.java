package com.common.event_common.mapper;

import com.common.event_common.domain_event_vo.shipment.ShipmentCanceledDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentCreatedDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentReturnedDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentStatusChangedDomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class ShipmentDomainEventMapper {
    private static final String RETURN = "반품";

    public ShipmentCreatedDomainEvent toShipmentCreatedEvent(Map<Long, Long> shipmentMap, Long orderId) {
        return ShipmentCreatedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .shipmentMap(shipmentMap)
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public ShipmentStatusChangedDomainEvent toShipmentStatusChangedEvent(Long shipmentId, String status) {
        return ShipmentStatusChangedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .status(status)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public ShipmentCanceledDomainEvent toShipmentCanceledEvent(Long shipmentId, Long orderId) {
        return ShipmentCanceledDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public ShipmentReturnedDomainEvent toShipmentReturnedEvent(Long shipmentId, Long orderItemId) {
        return ShipmentReturnedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .orderItemId(orderItemId)
                .cancelReason(RETURN)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
