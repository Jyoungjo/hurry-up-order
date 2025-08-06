package com.purchase.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ErrorResponseException errorResponseException) {
            log.warn("ErrorResponseException: {}", errorResponseException.getBody());

            response.setStatusCode(errorResponseException.getStatusCode());

            return writeErrorResponse(response, errorResponseException);
        }

        return Mono.error(ex);
    }

    private Mono<Void> writeErrorResponse(ServerHttpResponse response, ErrorResponseException ex) {
        DataBufferFactory bufferFactory = response.bufferFactory();

        return response.writeWith(Mono.fromSupplier(() -> {
            try {
                CustomErrorResponse customErrorResponse = CustomErrorResponse.defaultBuild(
                        ex.getBody().getDetail()
                );
                byte[] errorResponseBytes = objectMapper.writeValueAsBytes(customErrorResponse);
                return bufferFactory.wrap(errorResponseBytes);
            } catch (Exception e) {
                log.error("Error writing the response: ", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
