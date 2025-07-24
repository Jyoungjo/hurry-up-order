package com.purchase.preorder.order_service.api.internal;

import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.ReqReserveStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "item-service", url = "${feign.client.config.item-service.url}")
public interface ItemClient {
    @PostMapping("/item-service/api/v1/internal/items/batch")
    List<ItemResponse> getItems(@RequestBody List<Long> itemIds);

    @PostMapping("/item-service/api/v1/internal/stocks")
    void increaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);

    @PutMapping("/item-service/api/v1/internal/stocks")
    void decreaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity);

    @PostMapping("/item-service/api/v1/users/{userId}/stocks/reservations")
    void reserveStocks(@PathVariable Long userId, @RequestBody List<ReqReserveStockDto> reserveStockDtos);
}
