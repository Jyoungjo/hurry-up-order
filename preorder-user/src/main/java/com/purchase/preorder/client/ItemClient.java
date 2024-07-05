package com.purchase.preorder.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @GetMapping("/item-service/api/v1/internal/items/{itemId}")
    ItemResponse getItem(@PathVariable("itemId") Long itemId);
}
