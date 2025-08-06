package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.order.OrderReturnRequestedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderReturnRequestedDomainEventRetryHandler implements EventRetryHandler<OrderReturnRequestedDomainEvent> {

    private final ShipmentService shipmentService;

    @Override
    public String getEventType() {
        return DomainEventType.ORDER_RETURN_REQUESTED.name();
    }

    @Override
    public void handle(OrderReturnRequestedDomainEvent event) throws Exception {
        shipmentService.requestReturnShipments(event.getOrderId(), event.getOrderItemIds());
    }

    @Override
    public Class<OrderReturnRequestedDomainEvent> getEventClass() {
        return OrderReturnRequestedDomainEvent.class;
    }
}
