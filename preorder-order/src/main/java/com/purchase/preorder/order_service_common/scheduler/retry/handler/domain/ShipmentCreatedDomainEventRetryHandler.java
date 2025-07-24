package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.shipment.ShipmentCreatedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCreatedDomainEventRetryHandler implements EventRetryHandler<ShipmentCreatedDomainEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return DomainEventType.SHIPMENT_CREATED.name();
    }

    @Override
    public void handle(ShipmentCreatedDomainEvent event) throws Exception {
        orderService.assignShipments(event.getOrderId(), event.getShipmentMap());
    }

    @Override
    public Class<ShipmentCreatedDomainEvent> getEventClass() {
        return ShipmentCreatedDomainEvent.class;
    }
}
