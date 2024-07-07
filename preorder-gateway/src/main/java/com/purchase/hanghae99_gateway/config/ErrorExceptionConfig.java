package com.purchase.hanghae99_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.hanghae99_gateway.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ErrorExceptionConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public ErrorWebExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(objectMapper);
    }
}
