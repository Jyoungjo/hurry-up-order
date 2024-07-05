package com.purchase.preorder.stock;

import com.purchase.preorder.stock.dto.ReqStockDto;

public interface StockService {
    void increaseStock(Long itemId, int quantity);
    void increaseStock(ReqStockDto req);
    void decreaseStock(Long itemId, int quantity);
    void decreaseStock(ReqStockDto req);
    int getStockQuantity(Long itemId);
}
