package com.purchase.hanghae99_gateway;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {
    private final JwtValidator jwtValidator;
    private final RouteValidator routeValidator;

    public AuthorizationFilter(JwtValidator jwtValidator, RouteValidator routeValidator){
        super(Config.class);
        this.jwtValidator = jwtValidator;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (routeValidator.isSecured.test(request)) {
                String accessToken = getAccessTokenFromHeader(exchange);
                if (!checkAccessToken(accessToken)) {
                    return unauthorizedResponse(exchange);
                }
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                log.info("Custom Post filter: response code: " + response.getStatusCode());
            }));
        };
    }

    private String getAccessTokenFromHeader(ServerWebExchange exchange) {
        String name = "accessToken";

        return exchange.getRequest().getHeaders().get("Cookie").stream()
                .flatMap(cookie -> Arrays.stream(cookie.split("; ")))
                .filter(cookie -> cookie.startsWith(name))
                .map(cookie -> cookie.substring(name.length() + 1))
                .findFirst()
                .orElse(null);
    }

    private boolean checkAccessToken(String accessToken) {
        return StringUtils.hasText(accessToken) && isTokenValid(accessToken);
    }

    private boolean isTokenValid(String token) {
        try {
            return jwtValidator.isTokenValid(token);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    // 인증 실패 Response
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage = "{\"message\": \"잘못된 접근입니다.\"}";
        log.error(exchange.getResponse().getStatusCode().toString());

        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes());
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    // Config할 inner class -> 설정파일에 있는 args
    @Getter
    @Setter
    public static class Config{
        private String headerName;
        private String headerValue;
    }
}
