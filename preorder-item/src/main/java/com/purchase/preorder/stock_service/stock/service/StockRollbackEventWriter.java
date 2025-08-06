package com.purchase.preorder.stock_service.stock.service;

import com.common.event_common.domain_event_vo.stock.StockRedisRollbackRequestedDomainEvent;
import com.common.event_common.mapper.StockDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class StockRollbackEventWriter {

    private final DomainEventPublisher publisher;
    private final StockDomainEventMapper mapper;

    public StockRollbackEventWriter(@Qualifier("stockDomainEventPublisher") DomainEventPublisher publisher, StockDomainEventMapper mapper) {
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeRedisStockRollbackEvent(Long orderId, Map<Long, Integer> qtyMap) {
        StockRedisRollbackRequestedDomainEvent event = mapper.toStockRedisRollbackEvent(orderId, qtyMap);
        publisher.publishOnlySpringEventAfterCommit(event);
    }
}
