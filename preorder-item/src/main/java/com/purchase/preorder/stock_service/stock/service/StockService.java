package com.purchase.preorder.stock_service.stock.service;

import com.purchase.preorder.stock_service.stock.dto.ReqReserveStockDto;
import com.purchase.preorder.stock_service.stock.dto.ReqStockDto;

import java.util.List;
import java.util.Map;

public interface StockService {
    void createStock(Long itemId, int initQty);
    Integer getStockQuantity(Long itemId);
    Map<Long, Integer> getStocksByItemIds(List<Long> itemIds);
    void rollbackStocks(Map<Long, Integer> qtyMap);
    void rollbackRedisStocks(Long orderId, Map<Long, Integer> qtyMap);
    void reserveStocks(Long userId, List<ReqReserveStockDto> reserveStockDtos);
    void decreaseStock(Long userId, List<Long> itemIds, Long orderId);
    void increaseStock(ReqStockDto req);
    void decreaseStock(ReqStockDto req);
    void cancelReservationStock(Long userId, List<Long> itemIds, Long orderId);
    void deleteStock(Long itemId);
    void deleteStocks(List<Long> itemIds);
}
