package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.shipment.ShipmentCanceledDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCanceledDomainEventRetryHandler implements EventRetryHandler<ShipmentCanceledDomainEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return DomainEventType.SHIPMENT_CANCELED.name();
    }

    @Override
    public void handle(ShipmentCanceledDomainEvent event) throws Exception {
        orderService.onShipmentCancel(event.getOrderId(), event.getCancelReason());
    }

    @Override
    public Class<ShipmentCanceledDomainEvent> getEventClass() {
        return ShipmentCanceledDomainEvent.class;
    }
}
