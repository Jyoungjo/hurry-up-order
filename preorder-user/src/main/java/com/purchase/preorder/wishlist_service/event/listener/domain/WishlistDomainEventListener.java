package com.purchase.preorder.wishlist_service.event.listener.domain;

import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import com.purchase.preorder.wishlist_service.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WishlistDomainEventListener {

    private final WishlistService wishlistService;
    private final WishlistDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(UserDeletedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> wishlistService.delete(event.getUserId()));
    }
}
