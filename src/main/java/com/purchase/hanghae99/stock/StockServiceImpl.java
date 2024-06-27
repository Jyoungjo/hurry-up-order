package com.purchase.hanghae99.stock;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {
    private final ItemService itemService;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void increaseStock(Long itemId, int quantity) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseGet(() -> {
                    Item item = itemService.findItem(itemId);
                    Stock newStock = Stock.of(item, quantity);
                    return stockRepository.save(newStock);
                });
        stock.increaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void decreaseStock(Long itemId, int quantity) {
        Stock stock = stockRepository.findByItemId(itemId)
                        .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        if (stock.getQuantity() < quantity) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }

        stock.decreaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    public int getStockQuantity(Long itemId) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        return stock.getQuantity();
    }
}
