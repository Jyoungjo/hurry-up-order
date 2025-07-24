package com.purchase.preorder.item_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.item.ItemCreatedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemCreatedDomainEventRetryHandler implements EventRetryHandler<ItemCreatedDomainEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return DomainEventType.ITEM_CREATED.name();
    }

    @Override
    public void handle(ItemCreatedDomainEvent event) throws Exception {
        stockService.createStock(event.getItemId(), event.getInitQuantity());
    }

    @Override
    public Class<ItemCreatedDomainEvent> getEventClass() {
        return ItemCreatedDomainEvent.class;
    }
}
