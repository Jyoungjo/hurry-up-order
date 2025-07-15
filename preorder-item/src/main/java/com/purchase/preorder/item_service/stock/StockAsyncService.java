package com.purchase.preorder.item_service.stock;

import com.purchase.preorder.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_STOCK;

@Service
@RequiredArgsConstructor
public class StockAsyncService {
    private final StockRepository stockRepository;

    @Async
    public void asyncIncreaseStock(Long itemId, int quantity) {
        try {
            Stock stock = stockRepository.findByItemId(itemId)
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
            stock.increaseQuantity(quantity);
            stockRepository.save(stock);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Async
    public void asyncDecreaseStock(Long itemId, int quantity) {
        try {
            Stock stock = stockRepository.findByItemId(itemId)
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
            stock.decreaseQuantity(quantity);
            stockRepository.save(stock);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
