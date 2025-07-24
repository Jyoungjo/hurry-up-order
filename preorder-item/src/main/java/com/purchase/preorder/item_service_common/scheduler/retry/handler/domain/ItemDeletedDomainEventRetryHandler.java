package com.purchase.preorder.item_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.item.ItemDeletedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemDeletedDomainEventRetryHandler implements EventRetryHandler<ItemDeletedDomainEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return DomainEventType.ITEM_DELETED.name();
    }

    @Override
    public void handle(ItemDeletedDomainEvent event) throws Exception {
        stockService.deleteStock(event.getItemId());
    }

    @Override
    public Class<ItemDeletedDomainEvent> getEventClass() {
        return ItemDeletedDomainEvent.class;
    }
}
