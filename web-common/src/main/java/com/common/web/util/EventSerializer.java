package com.common.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

@Component
public class EventSerializer {
    private static final String SERIALIZE_ERROR = "serialization_failed";

    public static String serializeEvent(Object event) {
        try {
            return ObjectMapperProvider.get().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return SERIALIZE_ERROR;
        }
    }
}
