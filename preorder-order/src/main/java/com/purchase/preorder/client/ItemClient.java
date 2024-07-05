package com.purchase.preorder.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @GetMapping("/item-service/api/v1/internal/items/{itemId}")
    ItemResponse getItem(@PathVariable("itemId") Long itemId);

    @PostMapping("/item-service/api/v1/internal/stocks")
    void increaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);

    @PutMapping("/item-service/api/v1/internal/stocks")
    void decreaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);
}
