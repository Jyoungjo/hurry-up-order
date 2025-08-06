package com.common.event_common.retry;

import com.common.domain.entity.common.EventFailure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EventRetryScheduler {

    private final EventRetryHelper eventRetryHelper;
    private final EventRetryType type;

    public void execute() {
        List<EventFailure> failedEvents = eventRetryHelper.loadFailures();

        for (EventFailure failure : failedEvents) {
            try {
                eventRetryHelper.processEvent(failure);
                log.info("[{}-SERVICE] 이벤트 리트라이 성공 - eventType: {}", type, failure.getEventType());
            } catch (Exception e) {
                eventRetryHelper.markAsFailed(failure, e);
                log.warn("[{}-SERVICE] 이벤트 리트라이 실패 - eventType: {}, reason: {}", type, failure.getEventType(), e.getMessage());
            }
        }
    }
}
