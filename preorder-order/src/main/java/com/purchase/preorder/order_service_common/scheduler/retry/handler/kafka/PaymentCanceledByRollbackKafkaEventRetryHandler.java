package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCanceledByRollbackKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCanceledByRollbackKafkaEventRetryHandler implements EventRetryHandler<PaymentCanceledByRollbackKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCELED_BY_ROLLBACK;
    }

    @Override
    public void handle(PaymentCanceledByRollbackKafkaEvent event) throws Exception {
        orderService.updateStatusByRollback(event.getOrderId(), event.getCancelReason().toOrderStatus());
    }

    @Override
    public Class<PaymentCanceledByRollbackKafkaEvent> getEventClass() {
        return PaymentCanceledByRollbackKafkaEvent.class;
    }
}
