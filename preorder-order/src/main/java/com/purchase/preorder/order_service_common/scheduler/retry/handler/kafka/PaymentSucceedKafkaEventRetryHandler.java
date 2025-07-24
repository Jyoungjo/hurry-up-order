package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentSucceedKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSucceedKafkaEventRetryHandler implements EventRetryHandler<PaymentSucceedKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_SUCCEED;
    }

    @Override
    public void handle(PaymentSucceedKafkaEvent event) throws Exception {
        orderService.onPaymentSucceed(event.getOrderId());
    }

    @Override
    public Class<PaymentSucceedKafkaEvent> getEventClass() {
        return PaymentSucceedKafkaEvent.class;
    }
}
