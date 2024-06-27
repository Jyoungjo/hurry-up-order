package com.purchase.hanghae99.stock;

public interface StockService {
    void increaseStock(Long itemId, int quantity);
    void decreaseStock(Long itemId, int quantity);
    int getStockQuantity(Long itemId);
}
