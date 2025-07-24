package com.common.kafka.listener;

import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.log.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaEventListener {

    private final ProcessedEventService processedEventService;
    private final EventFailureService eventFailureService;
    /*
     * Default handler: 매핑된 @KafkaHandler가 없으면 이걸 호출
     */
    @KafkaHandler(isDefault = true)
    public void handleDefault(Object e) {
        log.warn("Unhandled event type: {}", e.getClass().getSimpleName());
    }

    protected final <T extends KafkaEvent> void dispatch(T e, Consumer<T> handler) {
        if (processedEventService.isProcessed(e.getEventId())) return;
        try {
            handler.accept(e);
        } catch (Exception ex) {
            eventFailureService.saveEventFailure(e, ex);
        }
    }
}
