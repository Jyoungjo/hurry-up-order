package com.purchase.preorder.payment_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByReturnKafkaEvent;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelRequestedByReturnKafkaEventRetryHandler implements EventRetryHandler<PaymentCancelRequestedByReturnKafkaEvent> {

    private final PaymentService paymentService;

    @Override
    public String getEventType() {
        return KafkaEventType.PAYMENT_CANCEL_REQUESTED_BY_RETURN;
    }

    @Override
    public void handle(PaymentCancelRequestedByReturnKafkaEvent event) throws Exception {
        paymentService.cancelPaymentByReturn(event.getShipmentId(), event.getOrderId(), event.getCancelReason());
    }

    @Override
    public Class<PaymentCancelRequestedByReturnKafkaEvent> getEventClass() {
        return PaymentCancelRequestedByReturnKafkaEvent.class;
    }
}
