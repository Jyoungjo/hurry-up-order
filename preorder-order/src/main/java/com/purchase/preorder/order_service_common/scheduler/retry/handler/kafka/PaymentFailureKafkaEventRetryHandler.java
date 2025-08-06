package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentFailureKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailureKafkaEventRetryHandler implements EventRetryHandler<PaymentFailureKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_FAILURE;
    }

    @Override
    public void handle(PaymentFailureKafkaEvent event) throws Exception {
        orderService.onPaymentFailure(event.getOrderId());
    }

    @Override
    public Class<PaymentFailureKafkaEvent> getEventClass() {
        return PaymentFailureKafkaEvent.class;
    }
}
