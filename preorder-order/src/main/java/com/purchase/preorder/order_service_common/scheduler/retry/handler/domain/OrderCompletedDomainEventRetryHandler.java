package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.order.OrderCompletedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCompletedDomainEventRetryHandler implements EventRetryHandler<OrderCompletedDomainEvent> {

    private final ShipmentService shipmentService;

    @Override
    public String getEventType() {
        return DomainEventType.ORDER_COMPLETED.name();
    }

    @Override
    public void handle(OrderCompletedDomainEvent event) throws Exception {
        shipmentService.createShipments(event.getOrderItemIds(), event.getOrderId());
    }

    @Override
    public Class<OrderCompletedDomainEvent> getEventClass() {
        return OrderCompletedDomainEvent.class;
    }
}
