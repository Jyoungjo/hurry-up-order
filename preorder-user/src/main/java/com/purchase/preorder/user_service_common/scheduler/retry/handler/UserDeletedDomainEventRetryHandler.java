package com.purchase.preorder.user_service_common.scheduler.retry.handler;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.wishlist_service.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletedDomainEventRetryHandler implements EventRetryHandler<UserDeletedDomainEvent> {

    private final WishlistService wishlistService;

    @Override
    public String getEventType() {
        return DomainEventType.USER_DELETED.name();
    }

    @Override
    public void handle(UserDeletedDomainEvent event) {
        wishlistService.delete(event.getUserId());
    }

    @Override
    public Class<UserDeletedDomainEvent> getEventClass() {
        return UserDeletedDomainEvent.class;
    }
}
