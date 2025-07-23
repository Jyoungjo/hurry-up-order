package com.purchase.preorder.payment_service.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossFeignConfig extends FeignConfig {
    @Value("${tossPayments.secret-key}")
    private String tossSecretKey;

    @Bean(name = "tossAuthInterceptor")
    @Override
    public RequestInterceptor authInterceptor() {
        return template -> {
            final String header = createAuthorizationHeader();
            template.header(AUTHORIZATION_HEADER, header);
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    protected String createAuthorizationHeader() {
        final byte[] encodedBytes = Base64.getEncoder().encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }
}
