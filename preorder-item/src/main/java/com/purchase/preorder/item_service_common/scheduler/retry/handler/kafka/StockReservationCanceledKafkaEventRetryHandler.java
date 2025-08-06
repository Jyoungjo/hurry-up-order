package com.purchase.preorder.item_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.stock.StockReservationCanceledKafkaEvent;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockReservationCanceledKafkaEventRetryHandler implements EventRetryHandler<StockReservationCanceledKafkaEvent> {

    private final StockService stockService;

    @Override
    public String getEventType() {
        return KafkaEventType.STOCK_RESERVATION_CANCELED;
    }

    @Override
    public void handle(StockReservationCanceledKafkaEvent event) throws Exception {
        stockService.cancelReservationStock(event.getUserId(), event.getItemIds(), event.getOrderId());
    }

    @Override
    public Class<StockReservationCanceledKafkaEvent> getEventClass() {
        return StockReservationCanceledKafkaEvent.class;
    }
}
