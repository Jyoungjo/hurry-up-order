package com.purchase.preorder.payment_service_common.config;

import feign.RequestInterceptor;

public abstract class FeignConfig {
    protected static final String AUTH_HEADER_PREFIX = "Basic ";
    protected static final String AUTHORIZATION_HEADER = "Authorization";

    public abstract RequestInterceptor authInterceptor();
    protected abstract String createAuthorizationHeader();
}
