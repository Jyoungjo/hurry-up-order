package com.common.kafka.log;

public interface ProcessedEventService {
    boolean isProcessed(String eventId);
}
