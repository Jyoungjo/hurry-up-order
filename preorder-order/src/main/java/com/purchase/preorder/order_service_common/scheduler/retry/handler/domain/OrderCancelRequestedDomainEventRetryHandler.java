package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.order.OrderCancelRequestedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelRequestedDomainEventRetryHandler implements EventRetryHandler<OrderCancelRequestedDomainEvent> {

    private final ShipmentService shipmentService;

    @Override
    public String getEventType() {
        return DomainEventType.ORDER_CANCEL_REQUESTED.name();
    }

    @Override
    public void handle(OrderCancelRequestedDomainEvent event) throws Exception {
        shipmentService.cancelShipments(event.getOrderId(), event.getOrderItemIds());
    }

    @Override
    public Class<OrderCancelRequestedDomainEvent> getEventClass() {
        return OrderCancelRequestedDomainEvent.class;
    }
}
