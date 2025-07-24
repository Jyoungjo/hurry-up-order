package com.purchase.preorder.payment_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByRollbackKafkaEvent;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelRequestedByRollbackKafkaEventRetryHandler implements EventRetryHandler<PaymentCancelRequestedByRollbackKafkaEvent> {

    private final PaymentService paymentService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK;
    }

    @Override
    public void handle(PaymentCancelRequestedByRollbackKafkaEvent event) throws Exception {
        paymentService.cancelPaymentByRollback(event.getOrderId(), event.getCancelReason());
    }

    @Override
    public Class<PaymentCancelRequestedByRollbackKafkaEvent> getEventClass() {
        return PaymentCancelRequestedByRollbackKafkaEvent.class;
    }
}
