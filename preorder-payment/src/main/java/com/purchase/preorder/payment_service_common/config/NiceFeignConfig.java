package com.purchase.preorder.payment_service_common.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class NiceFeignConfig extends FeignConfig {
    @Value("${nicePayments.client-key}")
    private String niceClientKey;

    @Value("${nicePayments.secret-key}")
    private String niceSecretKey;

    @Bean(name = "niceAuthInterceptor")
    @Override
    public RequestInterceptor authInterceptor() {
        return template -> {
            final String header = createAuthorizationHeader();
            template.header(AUTHORIZATION_HEADER, header);
        };
    }

    @Override
    protected String createAuthorizationHeader() {
        final byte[] encodedBytes = Base64.getEncoder().encode((niceClientKey + ":" + niceSecretKey).getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }
}
