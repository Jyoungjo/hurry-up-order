package com.purchase.preorder.stock_service.event.listener.kafka;

import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.stock.StockDecrKafkaEvent;
import com.common.kafka.event_vo.stock.StockReservationCanceledKafkaEvent;
import com.common.kafka.event_vo.stock.StockRollbackRequestedKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.listener.AbstractKafkaEventListener;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        topics = TopicNames.STOCK_EVENTS,
        groupId = GroupIds.KAFKA_GROUP_STOCK
)
public class StockKafkaEventListener extends AbstractKafkaEventListener {

    private final StockService stockService;

    public StockKafkaEventListener(ProcessedEventService processedEventService,
                                   EventFailureService eventFailureService,
                                   StockService stockService) {
        super(processedEventService, eventFailureService);
        this.stockService = stockService;
    }

    @KafkaHandler
    public void handle(StockDecrKafkaEvent e) {
        dispatch(e, ev -> stockService.decreaseStock(ev.getUserId(), ev.getItemIds(), ev.getOrderId()));
    }

    @KafkaHandler
    public void handle(StockReservationCanceledKafkaEvent e) {
        dispatch(e, ev -> stockService.cancelReservationStock(ev.getUserId(), ev.getItemIds(), ev.getOrderId()));
    }

    @KafkaHandler
    public void handle(StockRollbackRequestedKafkaEvent e) {
        dispatch(e, ev -> stockService.rollbackStocks(ev.getStockMap()));
    }
}
