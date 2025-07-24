package com.purchase.preorder.item_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.stock.StockRedisRollbackRequestedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRedisRollbackRequestedDomainEventRetryHandler implements EventRetryHandler<StockRedisRollbackRequestedDomainEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return DomainEventType.STOCK_REDIS_ROLLBACK_REQUESTED.name();
    }

    @Override
    public void handle(StockRedisRollbackRequestedDomainEvent event) throws Exception {
        stockService.rollbackRedisStocks(event.getOrderId(), event.getQtyMap());
    }

    @Override
    public Class<StockRedisRollbackRequestedDomainEvent> getEventClass() {
        return StockRedisRollbackRequestedDomainEvent.class;
    }
}
