package com.purchase.hanghae99_order.item;

import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_order.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99_order.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99_order.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99_order.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99_order.item.dto.update.ResUpdateItemDto;
import com.purchase.hanghae99_order.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final StockService stockService;

    @Override
    @Transactional
    public ResCreateItemDto createItem(ReqCreateItemDto req) {
        Item item = req.toEntity();
        Item savedItem = itemRepository.save(item);
        stockService.increaseStock(savedItem, req.getQuantity());
        return ResCreateItemDto.fromEntity(savedItem, stockService.getStockQuantity(item.getId()));
    }

    @Override
    public Page<ResReadItemDto> readAllItems(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAll(pageable)
                .map(item -> ResReadItemDto.fromEntity(item, stockService.getStockQuantity(item.getId())));
    }

    @Override
    public ResReadItemDto readItem(Long itemId) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        return ResReadItemDto.fromEntity(savedItem, stockService.getStockQuantity(itemId));
    }

    @Override
    @Transactional
    public ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        savedItem.updateInfo(req.getName(), req.getDescription(), req.getPrice());
        return ResUpdateItemDto.fromEntity(itemRepository.save(savedItem));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new BusinessException(NOT_FOUND_ITEM);
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
    }
}
