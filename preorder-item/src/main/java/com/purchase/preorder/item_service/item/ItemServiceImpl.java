package com.purchase.preorder.item_service.item;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.item_service.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item_service.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item_service.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item_service.item.dto.update.ReqUpdateItemDto;
import com.purchase.preorder.item_service.item.dto.update.ResUpdateItemDto;
import com.purchase.preorder.item_service.common.RedisCacheKey;
import com.purchase.preorder.item_service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_ITEM;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final StockService stockService;

    @Override
    @Transactional
    public ResCreateItemDto createItem(ReqCreateItemDto req) {
        Item item = Item.of(req);

        if (req.getIsReserved().equals(true)) {
             item = Item.of(req);
        }

        itemRepository.save(item);
        stockService.createStock(item, req.getQuantity());
        return ResCreateItemDto.fromEntity(item, req.getQuantity());
    }

    @Override
    public Page<ResReadItemDto> readAllItems(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAll(pageable)
                .map(item -> ResReadItemDto.fromEntity(item, stockService.getStockQuantity(item.getId()).getQuantity()));
    }

    @Override
    @Cacheable(key = "#itemId", cacheNames = RedisCacheKey.ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public ResReadItemDto readItem(Long itemId) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        return ResReadItemDto.fromEntity(savedItem, stockService.getStockQuantity(itemId).getQuantity());
    }

    @Override
    @Transactional
    @CachePut(key = "#itemId", cacheNames = RedisCacheKey.ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        savedItem.updateInfo(req.getName(), req.getDescription(), req.getPrice());
        return ResUpdateItemDto.fromEntity(itemRepository.save(savedItem));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#itemId", cacheNames = RedisCacheKey.ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new BusinessException(NOT_FOUND_ITEM);
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    @Cacheable(key = "#itemId", cacheNames = RedisCacheKey.ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public ResReadItemDto findItem(Long itemId) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        return ResReadItemDto.fromEntity(savedItem, stockService.getStockQuantity(itemId).getQuantity());
    }
}
