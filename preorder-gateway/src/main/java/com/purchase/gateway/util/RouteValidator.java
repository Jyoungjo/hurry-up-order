package com.purchase.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/eureka",
            "/user-service/api/v1/users",
            "/user-service/api/v1/users/login",
            "/item-service/errorful/", // 장애 상황 테스트
            "/user-service/error/", // 장애 상황 테스트
            "/payment-service/"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}