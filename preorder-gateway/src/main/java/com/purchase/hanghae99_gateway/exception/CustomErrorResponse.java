package com.purchase.hanghae99_gateway.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomErrorResponse {
    private final String errorMessage;
    private final LocalDateTime localDateTime;
    private final Map<String, Object> addtionInfos = new HashMap<>();

    public CustomErrorResponse(String errorMessage, LocalDateTime localDateTime) {
        this.errorMessage = errorMessage;
        this.localDateTime = localDateTime;
    }

    public static CustomErrorResponse defaultBuild(String errorMessage) {
        return new CustomErrorResponse(errorMessage, LocalDateTime.now());
    }
}
