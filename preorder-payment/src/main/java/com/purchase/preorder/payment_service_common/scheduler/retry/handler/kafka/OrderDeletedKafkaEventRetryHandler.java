package com.purchase.preorder.payment_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.order.OrderDeletedKafkaEvent;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDeletedKafkaEventRetryHandler implements EventRetryHandler<OrderDeletedKafkaEvent> {

    private final PaymentService paymentService;

    @Override
    public String getEventType() {
        return KafkaEventType.ORDER_DELETED;
    }

    @Override
    public void handle(OrderDeletedKafkaEvent event) throws Exception {
        paymentService.delete(event.getOrderId());
    }

    @Override
    public Class<OrderDeletedKafkaEvent> getEventClass() {
        return OrderDeletedKafkaEvent.class;
    }
}
