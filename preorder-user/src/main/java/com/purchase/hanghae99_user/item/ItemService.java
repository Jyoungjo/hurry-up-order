package com.purchase.hanghae99_user.item;

import com.purchase.hanghae99_user.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99_user.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99_user.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99_user.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99_user.item.dto.update.ResUpdateItemDto;
import org.springframework.data.domain.Page;

public interface ItemService {
    ResCreateItemDto createItem(ReqCreateItemDto req);
    Page<ResReadItemDto> readAllItems(Integer page, Integer size);
    ResReadItemDto readItem(Long itemId);
    ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req);
    void deleteItem(Long itemId);
    Item findItem(Long itemId);
}
