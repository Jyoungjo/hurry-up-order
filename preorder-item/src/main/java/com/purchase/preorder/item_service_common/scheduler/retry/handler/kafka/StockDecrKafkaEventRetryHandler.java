package com.purchase.preorder.item_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.stock.StockDecrKafkaEvent;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecrKafkaEventRetryHandler implements EventRetryHandler<StockDecrKafkaEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return KafkaEventType.STOCK_DECREASE_REQ;
    }

    @Override
    public void handle(StockDecrKafkaEvent event) throws Exception {
        stockService.decreaseStock(event.getUserId(), event.getItemIds(), event.getOrderId());
    }

    @Override
    public Class<StockDecrKafkaEvent> getEventClass() {
        return StockDecrKafkaEvent.class;
    }
}
