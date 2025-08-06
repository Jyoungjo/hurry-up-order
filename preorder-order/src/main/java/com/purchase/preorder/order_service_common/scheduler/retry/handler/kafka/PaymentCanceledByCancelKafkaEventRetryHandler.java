package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCanceledByCancelKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCanceledByCancelKafkaEventRetryHandler implements EventRetryHandler<PaymentCanceledByCancelKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCELED_BY_CANCEL;
    }

    @Override
    public void handle(PaymentCanceledByCancelKafkaEvent event) throws Exception {
        orderService.updateStatus(event.getOrderId(), event.getCancelReason().toOrderStatus());
    }

    @Override
    public Class<PaymentCanceledByCancelKafkaEvent> getEventClass() {
        return PaymentCanceledByCancelKafkaEvent.class;
    }
}
