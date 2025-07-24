package com.purchase.preorder.stock_service.stock.controller;

import com.purchase.preorder.stock_service.stock.dto.ReqReserveStockDto;
import com.purchase.preorder.stock_service.stock.dto.ReqStockDto;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-service/api/v1")
public class StockController {
    private final StockService stockService;

    // 관리자용
    @PostMapping("/stocks")
    public ResponseEntity<Void> increaseStock(@RequestBody ReqStockDto req) {
        stockService.increaseStock(req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/stocks")
    public ResponseEntity<Void> decreaseStock(@RequestBody ReqStockDto req) {
        stockService.decreaseStock(req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stocks/items/{itemId}")
    public ResponseEntity<Integer> getStock(@PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(stockService.getStockQuantity(itemId));
    }

    @PostMapping("/stocks/batch")
    public ResponseEntity<Map<Long, Integer>> getStocksByItemIds(@RequestBody List<Long> itemIds) {
        return ResponseEntity.ok(stockService.getStocksByItemIds(itemIds));
    }

    @DeleteMapping("/stocks/batch")
    public ResponseEntity<Void> deleteStocksByItemIds(@RequestBody List<Long> itemIds) {
        stockService.deleteStocks(itemIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/stocks/reservations")
    public ResponseEntity<Void> reserveStocks(@PathVariable Long userId, @RequestBody List<ReqReserveStockDto> reserveStockDtos) {
        stockService.reserveStocks(userId, reserveStockDtos);
        return ResponseEntity.noContent().build();
    }
}
