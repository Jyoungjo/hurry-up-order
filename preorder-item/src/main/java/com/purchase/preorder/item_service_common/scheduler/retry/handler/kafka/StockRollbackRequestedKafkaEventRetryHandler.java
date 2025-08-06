package com.purchase.preorder.item_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.stock.StockRollbackRequestedKafkaEvent;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRollbackRequestedKafkaEventRetryHandler implements EventRetryHandler<StockRollbackRequestedKafkaEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return KafkaEventType.STOCK_ROLLBACK_REQUESTED;
    }

    @Override
    public void handle(StockRollbackRequestedKafkaEvent event) throws Exception {
        stockService.rollbackStocks(event.getStockMap());
    }

    @Override
    public Class<StockRollbackRequestedKafkaEvent> getEventClass() {
        return StockRollbackRequestedKafkaEvent.class;
    }
}
