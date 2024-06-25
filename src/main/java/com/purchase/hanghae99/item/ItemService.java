package com.purchase.hanghae99.item;

import com.purchase.hanghae99.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99.item.dto.update.ResUpdateItemDto;
import org.springframework.data.domain.Page;

public interface ItemService {
    ResCreateItemDto createItem(ReqCreateItemDto req);
    Page<ResReadItemDto> readAllItems(Integer page, Integer size);
    ResReadItemDto readItem(Long itemId);
    ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req);
    void deleteItem(Long itemId);
}
