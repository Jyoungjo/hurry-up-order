package com.purchase.preorder.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @PostMapping("/item-service/api/v1/internal/items")
    List<ItemResponse> getItems(@RequestBody List<Long> itemIds);
}
