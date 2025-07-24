package com.purchase.preorder.stock_service.stock.service;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.RedisKeyHelper;
import com.common.domain.entity.item.Stock;
import com.common.domain.repository.item.StockRepository;
import com.common.event_common.domain_event_vo.stock.StockCreatedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockDecreasedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRedisRolledBackDomainEvent;
import com.common.event_common.mapper.StockDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.item_service_common.util.RedisService;
import com.purchase.preorder.stock_service.stock.dto.ReqReserveStockDto;
import com.purchase.preorder.stock_service.stock.dto.ReqStockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final RedisService redisService;
    private final DomainEventPublisher publisher;
    private final StockDomainEventMapper mapper;
    private final StockRollbackEventWriter stockRollbackEventWriter;

    public StockServiceImpl(StockRepository stockRepository,
                            RedisService redisService,
                            @Qualifier("stockDomainEventPublisher") DomainEventPublisher publisher,
                            StockDomainEventMapper mapper,
                            StockRollbackEventWriter stockRollbackEventWriter) {
        this.stockRepository = stockRepository;
        this.redisService = redisService;
        this.publisher = publisher;
        this.mapper = mapper;
        this.stockRollbackEventWriter = stockRollbackEventWriter;
    }

    @Override
    @Transactional
    public void createStock(Long itemId, int initQty) {
        Stock stock = stockRepository.save(Stock.of(itemId, initQty));

        StockCreatedDomainEvent event = mapper.toStockCreatedEvent(itemId, stock.getId());
        publisher.publishOnlySpringEventAfterCommit(event);
    }

    // 사용자 주문에 의한 재고 감소 (Redis + Async DB Update)
    @Override
    @Transactional
    public void decreaseStock(Long userId, List<Long> itemIds, Long orderId) {
        List<Object> decreasedResult = redisService.decreaseConfirmStocks(List.of(), getArgsForRedis(userId, itemIds));
        Map<Long, Integer> qtyMap = getQtyMap(decreasedResult);

        try {
            for (Map.Entry<Long, Integer> entry : qtyMap.entrySet()) {
                Long itemId = entry.getKey();
                int qty = entry.getValue();

                int updated = stockRepository.decreaseStock(itemId, qty);
                if (updated == 0) {
                    log.error("DB 재고 차감 실패 - itemId: {}, qty: {}", itemId, qty);
                    throw new BusinessException(ExceptionCode.NOT_ENOUGH_STOCK);
                }
            }

            StockDecreasedDomainEvent event = mapper.toStockDecreasedEvent(orderId);
            publisher.publishWithOutboxAfterCommit(event);
        } catch (Exception e) {
            stockRollbackEventWriter.writeRedisStockRollbackEvent(orderId, qtyMap);
            throw e;
        }
    }

    @Override
    @Transactional
    public void cancelReservationStock(Long userId, List<Long> itemIds, Long orderId) {
        List<Object> canceledResult = redisService.cancelReservationStocks(List.of(), getArgsForRedis(userId, itemIds));

        if (!canceledResult.getFirst().equals("1")) {
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }
    }

    // 관리자 수동 재고 증가 (DB 동기)
    @Override
    @Transactional
    public void increaseStock(ReqStockDto req) {
        redisService.adjustStockAtomically(RedisKeyHelper.stockKey(req.getItemId()), req.getQuantity());
    }

    // 관리자 수동 재고 감소 (DB 동기)
    @Override
    @Transactional
    public void decreaseStock(ReqStockDto req) {
        redisService.adjustStockAtomically(RedisKeyHelper.stockKey(req.getItemId()), -req.getQuantity());
    }

    @Override
    public Integer getStockQuantity(Long itemId) {
        return getStocksByItemIds(List.of(itemId)).getOrDefault(itemId, 0);
    }

    @Override
    public Map<Long, Integer> getStocksByItemIds(List<Long> itemIds) {
        List<String> redisKeys = itemIds.stream()
                .map(RedisKeyHelper::stockKey)
                .toList();
        List<Object> cachedValues = redisService.multiGet(redisKeys);

        Map<Long, Integer> resultMap = new HashMap<>();
        List<Long> missedItemIds = new ArrayList<>();

        for (int i = 0; i < itemIds.size(); i++) {
            Object value = cachedValues.get(i);

            if (value == null) {
                missedItemIds.add(itemIds.get(i));
                continue;
            }

            resultMap.put(itemIds.get(i), Integer.parseInt(value.toString()));
        }

        if (!missedItemIds.isEmpty()) {
            List<Stock> stocks = stockRepository.findByItemIdIn(missedItemIds);

            for (Stock stock : stocks) {
                resultMap.put(stock.getItemId(), stock.getQuantity());
                redisService.setValues(RedisKeyHelper.stockKey(stock.getId()), stock.getQuantity());
            }
        }

        return resultMap;
    }

    @Override
    @Transactional
    public void deleteStock(Long itemId) {
        stockRepository.deleteByItemIds(LocalDateTime.now(), itemId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    redisService.delete(RedisKeyHelper.stockKey(itemId));
                } catch (Exception e) {
                    log.warn("Redis 캐시 삭제 실패: stock.itemId = {}", itemId);
                }
            }
        });
    }

    @Override
    @Transactional
    public void deleteStocks(List<Long> itemIds) {
        List<Stock> stocks = stockRepository.findByItemIdIn(itemIds);
        if (stocks.isEmpty()) throw new BusinessException(ExceptionCode.NOT_FOUND_ITEM);

        stockRepository.deleteAll(stocks);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                stocks.forEach(stock -> {
                    try {
                        redisService.delete(RedisKeyHelper.stockKey(stock.getItemId()));
                    } catch (Exception e) {
                        log.warn("Redis 캐시 삭제 실패: itemId = {}, stockId = {}", stock.getItemId(), stock.getId());
                    }
                });
            }
        });
    }

    @Override
    @Transactional
    public void reserveStocks(Long userId, List<ReqReserveStockDto> reserveStockDtos) {
        List<String> args = buildArgs(userId, reserveStockDtos);

        List<Object> result = redisService.reserveStockAtomically(List.of(), args.toArray(new String[0]));
        if (isSuccess(result)) return;

        // 실패 원인이 "재고 미초기화"인 경우만 DB 조회 후 Redis 초기화
        if (containsStockNotInitialized(result)) {
            List<Long> missingItemIds = extractMissingItemIds(result);

            Map<Long, Integer> stockMapFromDB = stockRepository.findByItemIdIn(missingItemIds).stream()
                    .collect(Collectors.toMap(Stock::getItemId, Stock::getQuantity));

            Map<String, Integer> initRedisMap = stockMapFromDB.entrySet().stream()
                    .filter(entry -> !redisService.exists(RedisKeyHelper.stockKey(entry.getKey())))
                    .collect(Collectors.toMap(
                            entry -> RedisKeyHelper.stockKey(entry.getKey()),
                            Map.Entry::getValue
                    ));

            if (!initRedisMap.isEmpty()) redisService.setValues(initRedisMap);

            // 재시도
            result = redisService.reserveStockAtomically(List.of(), args.toArray(new String[0]));
            if (isSuccess(result)) return;
        }

        // 최종 실패 처리
        String reason = result.size() > 1 ? result.get(1).toString() : "Unknown";
        throw new BusinessException(reason, ExceptionCode.INTERNAL_SERVER_ERROR);
    }

    /*
     * 재고 DB 동기화 실패로 인한 REDIS 재고 복구
     */
    @Override
    public void rollbackRedisStocks(Long orderId, Map<Long, Integer> qtyMap) {
        List<String> args = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : qtyMap.entrySet()) {
            args.add(entry.getKey().toString());
            args.add(entry.getValue().toString());
        }

        if (redisService.rollbackStocks(List.of(), args.toArray(new String[0]))) {
            StockRedisRolledBackDomainEvent event = mapper.toStockRedisRolledBackEvent(orderId);
            publisher.publishWithOutboxAfterCommit(event);
        }
    }

    @Override
    @Transactional
    public void rollbackStocks(Map<Long, Integer> qtyMap) {
        List<String> args = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : qtyMap.entrySet()) {
            Long itemId = entry.getKey();
            int qty = entry.getValue();

            stockRepository.increaseStock(itemId, qty);
            args.add(entry.getKey().toString());
            args.add(entry.getValue().toString());
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisService.rollbackStocks(List.of(), args.toArray(new String[0]));
            }
        });
    }

    private Map<Long, Integer> getQtyMap(List<?> result) {
        Map<Long, Integer> qtyMap = new HashMap<>();
        if (result != null) {
            for (int i = 0; i < result.size(); i+=2) {
                Long itemId = Long.valueOf(result.get(i).toString());
                Integer qty = Integer.valueOf(result.get(i + 1).toString());
                qtyMap.put(itemId, qty);
            }
        }
        return qtyMap;
    }

    private String[] getArgsForRedis(Long userId, List<Long> itemIds) {
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));
        args.addAll(itemIds.stream().map(String::valueOf).toList());

        return args.toArray(new String[0]);
    }

    private List<String> buildArgs(Long userId, List<ReqReserveStockDto> dtos) {
        List<String> args = new ArrayList<>();
        args.add(userId.toString());
        args.add(String.valueOf(System.currentTimeMillis() / 1000));
        args.add("180");

        for (ReqReserveStockDto dto : dtos) {
            args.add(dto.getItemId().toString());
            args.add(dto.getQuantity().toString());
        }
        return args;
    }

    private boolean isSuccess(List<Object> result) {
        return Integer.parseInt(result.getFirst().toString()) == 1;
    }

    private boolean containsStockNotInitialized(List<Object> result) {
        return result.stream()
                .skip(1)
                .anyMatch(o -> o.toString().startsWith("Stock not initialized:"));
    }

    private List<Long> extractMissingItemIds(List<Object> result) {
        return result.stream()
                .skip(1)
                .filter(o -> o.toString().startsWith("Stock not initialized:"))
                .map(o -> o.toString().split(":")[1].trim())
                .map(Long::parseLong)
                .toList();
    }
}
