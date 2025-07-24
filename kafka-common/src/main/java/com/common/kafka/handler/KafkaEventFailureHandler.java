package com.common.kafka.handler;

import com.common.event_common.handler.AbstractEventFailureHandler;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.web.util.EventSerializer;

public abstract class KafkaEventFailureHandler extends AbstractEventFailureHandler<KafkaEvent> {

    @Override
    protected final String getEventType(KafkaEvent event) {
        return event.getEventType();
    }

    @Override
    protected final String serializeEvent(KafkaEvent event) {
        return EventSerializer.serializeEvent(event);
    }
}
