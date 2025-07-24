package com.common.event_common.retry;

import com.common.domain.entity.common.EventFailure;

import java.util.List;

public interface EventRetryHelper {
    void processEvent(EventFailure failure) throws Exception;
    List<EventFailure> loadFailures();
    void markAsFailed(EventFailure failure, Exception e);
}
