package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.shipment.ShipmentReturnedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentReturnedDomainEventRetryHandler implements EventRetryHandler<ShipmentReturnedDomainEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return DomainEventType.SHIPMENT_RETURNED.name();
    }

    @Override
    public void handle(ShipmentReturnedDomainEvent event) throws Exception {
        orderService.onShipmentReturn(event.getShipmentId(), event.getOrderItemId(), event.getCancelReason());
    }

    @Override
    public Class<ShipmentReturnedDomainEvent> getEventClass() {
        return ShipmentReturnedDomainEvent.class;
    }
}
