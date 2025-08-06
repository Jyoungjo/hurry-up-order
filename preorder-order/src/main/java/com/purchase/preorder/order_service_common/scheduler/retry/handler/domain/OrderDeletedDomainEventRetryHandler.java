package com.purchase.preorder.order_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.order.OrderDeletedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.cart_service.cart.service.CartService;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDeletedDomainEventRetryHandler implements EventRetryHandler<OrderDeletedDomainEvent> {

    private final ShipmentService shipmentService;
    private final CartService cartService;

    @Override
    public String getEventType() {
        return DomainEventType.ORDER_DELETED.name();
    }

    @Override
    public void handle(OrderDeletedDomainEvent event) throws Exception {
        shipmentService.delete(event.getOrderItemIds());
        cartService.delete(event.getUserId());
    }

    @Override
    public Class<OrderDeletedDomainEvent> getEventClass() {
        return OrderDeletedDomainEvent.class;
    }
}
