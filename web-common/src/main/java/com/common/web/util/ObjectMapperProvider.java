package com.common.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {
    private static ObjectMapper objectMapper;

    public static void set(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public static ObjectMapper get() {
        if (objectMapper == null) throw new IllegalStateException("ObjectMapper is not initialized");
        return objectMapper;
    }
}
