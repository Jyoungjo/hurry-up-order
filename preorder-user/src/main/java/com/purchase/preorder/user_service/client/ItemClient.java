package com.purchase.preorder.user_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @GetMapping("/item-service/api/v1/internal/items/{itemId}")
    ItemResponse getItem(@PathVariable("itemId") Long itemId);

    // 회복탄력성 테스트를 위한 feign client
    @GetMapping("/item-service/errorful/case1")
    @CircuitBreaker(name = "case1", fallbackMethod = "fallback")
    @Retry(name = "case1", fallbackMethod = "fallback")
    String case1();

    @GetMapping("/item-service/errorful/case2")
    @CircuitBreaker(name = "case2", fallbackMethod = "fallback")
    @Retry(name = "case2", fallbackMethod = "fallback")
    String case2();

    @GetMapping("/item-service/errorful/case3")
    @CircuitBreaker(name = "case3", fallbackMethod = "fallback")
    @Retry(name = "case3", fallbackMethod = "fallback")
    String case3();

    default String fallback(Throwable t) {
        return "Fallback response due to: " + t.getMessage();
    }
}
