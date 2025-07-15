package com.purchase.preorder.item_service.stock;

import com.purchase.preorder.item.Item;
import com.purchase.preorder.item_service.stock.dto.ReqStockDto;
import com.purchase.preorder.item_service.stock.dto.ResStockDto;

public interface StockService {
    void createStock(Item item, int quantity);
    void increaseStock(Long itemId, int quantity);
    void increaseStock(ReqStockDto req);
    void decreaseStock(Long itemId, int quantity);
    void decreaseStock(ReqStockDto req);
    ResStockDto getStockQuantity(Long itemId);
}
