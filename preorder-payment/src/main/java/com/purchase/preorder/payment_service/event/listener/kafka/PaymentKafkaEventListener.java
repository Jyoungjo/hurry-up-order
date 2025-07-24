package com.purchase.preorder.payment_service.event.listener.kafka;

import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.order.OrderDeletedKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByCancelKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByReturnKafkaEvent;
import com.common.kafka.event_vo.payment.PaymentCancelRequestedByRollbackKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventListener {

    private final PaymentService paymentService;
    private final ProcessedEventService processedEventService;
    private final EventFailureService eventFailureService;

    @KafkaListener(topics = TopicNames.PAYMENT_CANCEL_REQUESTED_BY_CANCEL, groupId = GroupIds.KAFKA_GROUP_PAYMENT)
    public void listen(PaymentCancelRequestedByCancelKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            paymentService.cancelPaymentByCancel(event.getOrderId(), event.getCancelReason());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_CANCEL_REQUESTED_BY_RETURN, groupId = GroupIds.KAFKA_GROUP_PAYMENT)
    public void listen(PaymentCancelRequestedByReturnKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            paymentService.cancelPaymentByReturn(event.getShipmentId(), event.getOrderId(), event.getCancelReason());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK, groupId = GroupIds.KAFKA_GROUP_PAYMENT)
    public void listen(PaymentCancelRequestedByRollbackKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            paymentService.cancelPaymentByRollback(event.getOrderId(), event.getCancelReason());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.ORDER_DELETED, groupId = GroupIds.KAFKA_GROUP_PAYMENT)
    public void listen(OrderDeletedKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            paymentService.delete(event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }
}
