package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.domain.common.OrderStatus;
import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.stock.StockDecreasedKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreasedKafkaEventRetryHandler implements EventRetryHandler<StockDecreasedKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.STOCK_DECREASED;
    }

    @Override
    public void handle(StockDecreasedKafkaEvent event) throws Exception {
        orderService.updateStatus(event.getOrderId(), OrderStatus.PAID);
    }

    @Override
    public Class<StockDecreasedKafkaEvent> getEventClass() {
        return StockDecreasedKafkaEvent.class;
    }
}
