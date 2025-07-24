package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.shipment.ShipmentStatusChangedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentStatusChangedDomainEventRetryHandler implements EventRetryHandler<ShipmentStatusChangedDomainEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return DomainEventType.SHIPMENT_STATUS_CHANGED.name();
    }

    @Override
    public void handle(ShipmentStatusChangedDomainEvent event) throws Exception {
        orderService.updateStatusByShipment(event.getShipmentId(), event.getStatus());
    }

    @Override
    public Class<ShipmentStatusChangedDomainEvent> getEventClass() {
        return ShipmentStatusChangedDomainEvent.class;
    }
}
