package com.purchase.preorder.item_service.stock;

import com.purchase.preorder.item_service.stock.dto.ReqStockDto;
import com.purchase.preorder.item_service.stock.dto.ResStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-service/api/v1")
public class StockController {
    private final StockService stockService;

    @PostMapping("/stocks")
    public ResponseEntity<Void> increaseStock(@RequestBody ReqStockDto req) {
        stockService.increaseStock(req.getItemId(), req.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/stocks")
    public ResponseEntity<Void> decreaseStock(@RequestBody ReqStockDto req) {
        stockService.decreaseStock(req.getItemId(), req.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/internal/stocks")
    public ResponseEntity<Void> increaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        stockService.increaseStock(itemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/internal/stocks")
    public ResponseEntity<Void> decreaseStock(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        stockService.decreaseStock(itemId, quantity);
        return ResponseEntity.noContent().build();
    }

    // TODO 테스트 진행
    @GetMapping("/stocks/items/{itemId}")
    public ResponseEntity<ResStockDto> getStockQuantity(@PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(stockService.getStockQuantity(itemId));
    }
}
