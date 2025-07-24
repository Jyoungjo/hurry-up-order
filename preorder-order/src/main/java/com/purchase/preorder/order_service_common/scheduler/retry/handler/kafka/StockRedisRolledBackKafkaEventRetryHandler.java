package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.stock.StockRedisRolledBackKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRedisRolledBackKafkaEventRetryHandler implements EventRetryHandler<StockRedisRolledBackKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.STOCK_REDIS_ROLLED_BACK;
    }

    @Override
    public void handle(StockRedisRolledBackKafkaEvent event) throws Exception {
        orderService.onRedisRolledBack(event.getOrderId());
    }

    @Override
    public Class<StockRedisRolledBackKafkaEvent> getEventClass() {
        return StockRedisRolledBackKafkaEvent.class;
    }
}
