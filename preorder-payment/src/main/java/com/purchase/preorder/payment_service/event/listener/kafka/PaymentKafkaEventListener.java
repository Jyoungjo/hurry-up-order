package com.purchase.preorder.payment_service.event.listener.kafka;

import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.order.OrderDeletedKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByCancelKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByReturnKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByRollbackKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.listener.AbstractKafkaEventListener;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        topics = TopicNames.PAYMENT_EVENTS,
        groupId = GroupIds.KAFKA_GROUP_PAYMENT
)
public class PaymentKafkaEventListener extends AbstractKafkaEventListener {

    private final PaymentService paymentService;

    public PaymentKafkaEventListener(ProcessedEventService processedEventService,
                                     EventFailureService eventFailureService,
                                     PaymentService paymentService) {
        super(processedEventService, eventFailureService);
        this.paymentService = paymentService;
    }

    @KafkaHandler
    public void handle(PaymentCancelRequestedByCancelKafkaEvent e) {
        dispatch(e, ev -> paymentService.cancelPaymentByCancel(ev.getOrderId(), ev.getCancelReason()));
    }

    @KafkaHandler
    public void handle(PaymentCancelRequestedByReturnKafkaEvent e) {
        dispatch(e, ev -> paymentService.cancelPaymentByReturn(ev.getShipmentId(), ev.getOrderId(), ev.getCancelReason()));
    }

    @KafkaHandler
    public void handle(PaymentCancelRequestedByRollbackKafkaEvent e) {
        dispatch(e, ev -> paymentService.cancelPaymentByRollback(ev.getOrderId(), ev.getCancelReason()));
    }

    @KafkaHandler
    public void handle(OrderDeletedKafkaEvent e) {
        dispatch(e, ev -> paymentService.delete(ev.getOrderId()));
    }
}
