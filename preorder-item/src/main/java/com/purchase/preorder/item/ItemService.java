package com.purchase.preorder.item;

import com.purchase.preorder.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item.dto.update.ReqUpdateItemDto;
import com.purchase.preorder.item.dto.update.ResUpdateItemDto;
import org.springframework.data.domain.Page;

public interface ItemService {
    ResCreateItemDto createItem(ReqCreateItemDto req);
    Page<ResReadItemDto> readAllItems(Integer page, Integer size);
    ResReadItemDto readItem(Long itemId);
    ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req);
    void deleteItem(Long itemId);
    Item findItem(Long itemId);
}
