package com.purchase.preorder.item_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.stock.StockCreatedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.item_service.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockCreatedDomainEventRetryHandler implements EventRetryHandler<StockCreatedDomainEvent> {

    private final ItemService itemService;

    @Override
    public String getEventType() {
        return DomainEventType.STOCK_CREATED.name();
    }

    @Override
    public void handle(StockCreatedDomainEvent event) throws Exception {
        itemService.assignStock(event.getItemId(), event.getStockId());
    }

    @Override
    public Class<StockCreatedDomainEvent> getEventClass() {
        return StockCreatedDomainEvent.class;
    }
}
