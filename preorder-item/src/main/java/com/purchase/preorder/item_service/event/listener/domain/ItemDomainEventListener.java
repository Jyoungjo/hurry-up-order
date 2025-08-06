package com.purchase.preorder.item_service.event.listener.domain;

import com.common.event_common.domain_event_vo.stock.StockCreatedDomainEvent;
import com.purchase.preorder.item_service.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemDomainEventListener {

    private final ItemService itemService;
    private final ItemDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(StockCreatedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> itemService.assignStock(event.getItemId(), event.getStockId()));
    }
}
