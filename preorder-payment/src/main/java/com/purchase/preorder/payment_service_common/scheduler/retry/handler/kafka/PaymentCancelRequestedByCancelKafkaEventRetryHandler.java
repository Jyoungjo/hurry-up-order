package com.purchase.preorder.payment_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByCancelKafkaEvent;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelRequestedByCancelKafkaEventRetryHandler implements EventRetryHandler<PaymentCancelRequestedByCancelKafkaEvent> {

    private final PaymentService paymentService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_CANCEL;
    }

    @Override
    public void handle(PaymentCancelRequestedByCancelKafkaEvent event) throws Exception {
        paymentService.cancelPaymentByCancel(event.getOrderId(), event.getCancelReason());
    }

    @Override
    public Class<PaymentCancelRequestedByCancelKafkaEvent> getEventClass() {
        return PaymentCancelRequestedByCancelKafkaEvent.class;
    }
}
