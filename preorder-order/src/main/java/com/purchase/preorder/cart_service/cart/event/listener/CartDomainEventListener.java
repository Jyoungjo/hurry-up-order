package com.purchase.preorder.cart_service.cart.event.listener;

import com.common.event_common.domain_event_vo.order.OrderDeletedDomainEvent;
import com.purchase.preorder.cart_service.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartDomainEventListener {

    private final CartService cartService;
    private final CartDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderDeletedDomainEvent domainEvent) {
        helper.executeWithFailureHandling(domainEvent, () -> cartService.delete(domainEvent.getUserId()));
    }
}
