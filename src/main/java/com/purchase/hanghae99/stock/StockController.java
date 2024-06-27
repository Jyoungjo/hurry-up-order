package com.purchase.hanghae99.stock;

import com.purchase.hanghae99.stock.dto.ReqStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<Void> increaseStock(@RequestBody ReqStockDto req) {
        stockService.increaseStock(req.getItemId(), req.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> decreaseStock(@RequestBody ReqStockDto req) {
        stockService.decreaseStock(req.getItemId(), req.getQuantity());
        return ResponseEntity.noContent().build();
    }
}
