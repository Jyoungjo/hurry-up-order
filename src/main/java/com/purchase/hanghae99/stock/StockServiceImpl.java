package com.purchase.hanghae99.stock;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    @Override
    public void increaseStock(Item item, int quantity) {
        Stock stock = stockRepository.findByItem(item)
                .orElseGet(() -> {
                    Stock newStock = Stock.of(item, 0);
                    return stockRepository.save(newStock);
                });
        stock.increaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void increaseStock(Long itemId, int quantity) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        stock.increaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    public void decreaseStock(Item item, int quantity) {
        Stock stock = stockRepository.findByItem(item)
                        .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        if (stock.getQuantity() < quantity) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }

        stock.decreaseQuantity(quantity);
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
