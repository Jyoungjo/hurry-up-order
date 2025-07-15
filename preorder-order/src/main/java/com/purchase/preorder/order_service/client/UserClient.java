package com.purchase.preorder.order_service.client;

import com.purchase.preorder.order_service.client.response.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${feign.client.config.user-service.url}")
public interface UserClient {
    @GetMapping("/user-service/api/v1/internal/users")
    @CircuitBreaker(name = "getUserByEmail", fallbackMethod = "fallback")
    @Retry(name = "getUserByEmail", fallbackMethod = "fallback")
    UserResponse getUserByEmail(@RequestParam("email") String email);

    default String fallback(Throwable t) {
        return "Fallback response due to: " + t.getMessage();
    }
}
