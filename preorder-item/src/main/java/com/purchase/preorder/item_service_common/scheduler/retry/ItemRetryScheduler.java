package com.purchase.preorder.item_service_common.scheduler.retry;

import com.common.event_common.retry.EventRetryHelper;
import com.common.event_common.retry.EventRetryScheduler;
import com.common.event_common.retry.EventRetryType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ItemRetryScheduler {

    private final EventRetryScheduler eventRetryScheduler;

    public ItemRetryScheduler(EventRetryHelper helper) {
        this.eventRetryScheduler = new EventRetryScheduler(helper, EventRetryType.ITEM);
    }

    @Scheduled(fixedDelay = 30000)
    public void retryFailedEvents() {
        eventRetryScheduler.execute();
    }
}
