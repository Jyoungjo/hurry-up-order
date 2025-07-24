package com.purchase.preorder.item_service.item.service;

import com.purchase.preorder.item_service.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item_service.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item_service.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item_service.item.dto.update.ReqUpdateItemDto;
import com.purchase.preorder.item_service.item.dto.update.ResUpdateItemDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    ResCreateItemDto createItem(ReqCreateItemDto req);
    Page<ResReadItemDto> readAllItems(Integer page, Integer size);
    List<ResReadItemDto> readItems(List<Long> itemIds);
    ResReadItemDto readItem(Long itemId);
    ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req);
    void deleteItems(List<Long> itemIds);
    void deleteItem(Long itemId);
    void assignStock(Long itemId, Long stockId);
}
