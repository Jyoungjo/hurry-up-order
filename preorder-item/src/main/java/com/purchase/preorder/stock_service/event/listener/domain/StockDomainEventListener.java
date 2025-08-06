package com.purchase.preorder.stock_service.event.listener.domain;

import com.common.event_common.domain_event_vo.item.ItemCreatedDomainEvent;
import com.common.event_common.domain_event_vo.item.ItemDeletedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockDecreasedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRollbackRequestedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRolledBackDomainEvent;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockDomainEventListener {

    private final StockService stockService;
    private final StockDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ItemCreatedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> stockService.createStock(event.getItemId(), event.getInitQuantity()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ItemDeletedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> stockService.deleteStock(event.getItemId()));
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockDecreasedDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockRedisRollbackRequestedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> stockService.rollbackRedisStocks(event.getOrderId(), event.getQtyMap()));
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockRedisRolledBackDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }
}
