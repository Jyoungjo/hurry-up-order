package com.purchase.hanghae99_user.stock;

import com.purchase.hanghae99_user.item.Item;

public interface StockService {
    void increaseStock(Item item, int quantity);
    void increaseStock(Long itemId, int quantity);
    void decreaseStock(Item item, int quantity);
    void decreaseStock(Long itemId, int quantity);
    int getStockQuantity(Long itemId);
}
