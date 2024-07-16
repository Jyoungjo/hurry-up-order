package com.purchase.preorder.client;

import com.purchase.preorder.client.response.ItemResponse;
import com.purchase.preorder.client.response.StockResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @GetMapping("/item-service/api/v1/internal/items/{itemId}")
    @CircuitBreaker(name = "getItem", fallbackMethod = "fallback")
    @Retry(name = "getItem", fallbackMethod = "fallback")
    ItemResponse getItem(@PathVariable("itemId") Long itemId);

    @PostMapping("/item-service/api/v1/internal/stocks")
    @CircuitBreaker(name = "increaseStock", fallbackMethod = "fallback")
    @Retry(name = "increaseStock", fallbackMethod = "fallback")
    void increaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);

    @PutMapping("/item-service/api/v1/internal/stocks")
    @CircuitBreaker(name = "decreaseStock", fallbackMethod = "fallback")
    @Retry(name = "decreaseStock", fallbackMethod = "fallback")
    void decreaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);

    @GetMapping("/item-service/api/v1/stocks/items/{itemId}")
    @CircuitBreaker(name = "getStock", fallbackMethod = "fallback")
    @Retry(name = "getStock", fallbackMethod = "fallback")
    StockResponse getStock(@PathVariable("itemId") Long itemId);

    default String fallback(Throwable t) {
        return "Fallback response due to: " + t.getMessage();
    }
}
