package com.common.kafka.mapper;

import com.common.domain.entity.common.OutboxMessage;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.web.util.ObjectMapperProvider;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxMapper {
    public static OutboxMessage toOutboxMessage(KafkaEvent event) {
        String typeId = getTypeFromAnnotation(event);

        return OutboxMessage.builder()
                ._type(typeId)
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .aggregateId(event.getAggregateId())
                .topic(event.getTopic())
                .payload(toJson(event))
                .occurredAt(event.getOccurredAt())
                .published(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static String getTypeFromAnnotation(KafkaEvent event) {
        JsonTypeInfo info = KafkaEvent.class.getAnnotation(JsonTypeInfo.class);
        if (info != null && info.property().equals("_type")) {
            JsonSubTypes subTypes = KafkaEvent.class.getAnnotation(JsonSubTypes.class);
            if (subTypes != null) {
                for (JsonSubTypes.Type t : subTypes.value()) {
                    if (t.value().equals(event.getClass())) return t.name();
                }
            }
        }
        throw new IllegalStateException("KafkaEvent type 정보 추출 실패: " + event.getClass());
    }

    private static String toJson(KafkaEvent event) {
        try {
            return ObjectMapperProvider.get().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("KafkaEvent 직렬화 실패", e);
        }
    }
}
