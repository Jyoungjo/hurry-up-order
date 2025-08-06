package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCanceledByReturnKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCanceledByReturnKafkaEventRetryHandler implements EventRetryHandler<PaymentCanceledByReturnKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCELED_BY_RETURN;
    }

    @Override
    public void handle(PaymentCanceledByReturnKafkaEvent event) throws Exception {
        orderService.updateStatus(event.getOrderId(), event.getCancelReason().toOrderStatus());
    }

    @Override
    public Class<PaymentCanceledByReturnKafkaEvent> getEventClass() {
        return PaymentCanceledByReturnKafkaEvent.class;
    }
}
