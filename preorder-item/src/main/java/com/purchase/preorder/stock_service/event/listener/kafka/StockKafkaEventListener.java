package com.purchase.preorder.stock_service.event.listener.kafka;

import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.stock.StockDecrKafkaEvent;
import com.common.kafka.event_vo.stock.StockReservationCanceledKafkaEvent;
import com.common.kafka.event_vo.stock.StockRollbackRequestedKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockKafkaEventListener {

    private final StockService stockService;
    private final EventFailureService eventFailureService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(topics = TopicNames.STOCK_DECR, groupId = GroupIds.KAFKA_GROUP_STOCK)
    public void listen(StockDecrKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            stockService.decreaseStock(event.getUserId(), event.getItemIds(), event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.STOCK_RESERVATION_CANCELED, groupId = GroupIds.KAFKA_GROUP_STOCK)
    public void listen(StockReservationCanceledKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            stockService.cancelReservationStock(event.getUserId(), event.getItemIds(), event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.STOCK_ROLLBACK_REQUESTED, groupId = GroupIds.KAFKA_GROUP_STOCK)
    public void listen(StockRollbackRequestedKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            stockService.rollbackStocks(event.getStockMap());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }
}
