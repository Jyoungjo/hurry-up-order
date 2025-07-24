package com.purchase.preorder.item_service.item.service;

import com.common.core.util.RedisKeyHelper;
import com.common.domain.entity.item.Item;
import com.common.domain.repository.item.ItemRepository;
import com.common.event_common.domain_event_vo.item.ItemCreatedDomainEvent;
import com.common.event_common.domain_event_vo.item.ItemDeletedDomainEvent;
import com.common.event_common.mapper.ItemDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.common.web.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.item_service.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item_service.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item_service.item.dto.read.ItemCacheDto;
import com.purchase.preorder.item_service.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item_service.item.dto.update.ReqUpdateItemDto;
import com.purchase.preorder.item_service.item.dto.update.ResUpdateItemDto;
import com.purchase.preorder.item_service_common.util.RedisService;
import com.purchase.preorder.stock_service.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.core.exception.ExceptionCode.NOT_FOUND_ITEM;
import static com.purchase.preorder.item_service_common.util.RedisCacheKey.ITEM_KEY_PREFIX;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final StockService stockService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final DomainEventPublisher eventPublisher;
    private final ItemDomainEventMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           StockService stockService,
                           RedisService redisService,
                           ObjectMapper objectMapper,
                           @Qualifier("itemDomainEventPublisher") DomainEventPublisher eventPublisher,
                           ItemDomainEventMapper mapper) {
        this.itemRepository = itemRepository;
        this.stockService = stockService;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResCreateItemDto createItem(ReqCreateItemDto req) {
        Item savedItem = itemRepository.save(
                Item.of(req.getName(), req.getDescription(), req.getPrice(), req.getOpenTime(), req.getIsReserved())
        );

        ItemCreatedDomainEvent event = mapper.toItemCreatedEvent(savedItem.getId(), req.getQuantity());
        eventPublisher.publishOnlySpringEventAfterCommit(event);

        return ResCreateItemDto.of(savedItem, req.getQuantity());
    }

    @Override
    public Page<ResReadItemDto> readAllItems(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemPage = itemRepository.findAll(pageable);

        List<Long> itemIds = itemPage.stream()
                .map(Item::getId)
                .toList();

        Map<Long, Integer> stockMap = stockService.getStocksByItemIds(itemIds);

        return itemPage.map(item ->
                ResReadItemDto.fromEntity(item, stockMap.getOrDefault(item.getId(), 0))
        );
    }

    @Override
    @Cacheable(key = "#itemId", cacheNames = ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public ResReadItemDto readItem(Long itemId) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        Integer quantity = stockService.getStockQuantity(savedItem.getId());
        return ResReadItemDto.fromEntity(savedItem, quantity != null ? quantity : 0);
    }

    @Override
    public List<ResReadItemDto> readItems(List<Long> itemIds) {
        List<String> redisKeys = itemIds.stream()
                .map(RedisKeyHelper::itemKey)
                .toList();

        List<Object> cachedObjects = redisService.multiGet(redisKeys);

        Map<Long, ResReadItemDto> resultMap = new HashMap<>();
        List<Long> idsToFetchFromDB = new ArrayList<>();

        for (int i = 0; i < itemIds.size(); i++) {
            Object cached = cachedObjects.get(i);
            Long itemId = itemIds.get(i);

            if (cached != null) {
                ResReadItemDto dto = objectMapper.convertValue(cached, ResReadItemDto.class);
                resultMap.put(itemId, dto);
                continue;
            }

            idsToFetchFromDB.add(itemId);
        }

        if (!idsToFetchFromDB.isEmpty()) {
            List<Item> items = itemRepository.findByIdIn(idsToFetchFromDB);
            Map<Long, Integer> stockMap = stockService.getStocksByItemIds(idsToFetchFromDB);

            for (Item item : items) {
                ResReadItemDto dto = ResReadItemDto.fromEntity(item, stockMap.getOrDefault(item.getId(), 0));
                resultMap.put(item.getId(), dto);
                redisService.setValues(RedisKeyHelper.itemKey(item.getId()), dto);
            }
        }

        return itemIds.stream()
                .map(resultMap::get)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(key = "#itemId", cacheNames = ITEM_KEY_PREFIX, cacheManager = "redisCacheManager")
    public ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));

        savedItem.updateInfo(req.getName(), req.getDescription(), req.getPrice());

        Integer quantity = stockService.getStockQuantity(itemId);
        ItemCacheDto itemCache = ItemCacheDto.of(savedItem, quantity);

        redisService.setValues(RedisKeyHelper.itemKey(savedItem.getId()), itemCache);

        return ResUpdateItemDto.fromEntity(savedItem);
    }

    /*
     * 단건 삭제 메서드
     */
    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        deleteItems(List.of(itemId));
    }

    @Override
    @Transactional
    public void assignStock(Long itemId, Long stockId) {
        itemRepository.assignStock(stockId, itemId);
    }

    @Override
    @Transactional
    public void deleteItems(List<Long> itemIds) {
        itemRepository.deleteByIds(LocalDateTime.now(), itemIds);

        for (Long itemId : itemIds) {
            ItemDeletedDomainEvent event = mapper.toItemDeletedEvent(itemId);
            eventPublisher.publishOnlySpringEventAfterCommit(event);
        }

        // 캐시에 등록된 상품들 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                itemIds.parallelStream().forEach(id -> {
                    try {
                        redisService.delete(RedisKeyHelper.itemKey(id));
                    } catch (Exception e) {
                        log.warn("Redis 캐시 삭제 실패: itemId = {}", id);
                    }
                });
            }
        });
    }
}
