package com.purchase.preorder.order_service.event.listener.kafka;

import com.common.domain.common.OrderStatus;
import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.payment.*;
import com.common.kafka.event_vo.stock.StockDecreasedKafkaEvent;
import com.common.kafka.event_vo.stock.StockRedisRolledBackKafkaEvent;
import com.common.kafka.event_vo.user.UserDeletedKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventListener {
    private final OrderService orderService;
    private final ProcessedEventService processedEventService;
    private final EventFailureService eventFailureService;

    @KafkaListener(topics = TopicNames.PAYMENT_SUCCEED, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(PaymentSucceedKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.onPaymentSucceed(event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_FAILURE, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(PaymentFailureKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.onPaymentFailure(event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.STOCK_DECREASED, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(StockDecreasedKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.updateStatus(event.getOrderId(), OrderStatus.PAID);
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_CANCELED_BY_CANCEL, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(PaymentCanceledByCancelKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.updateStatus(event.getOrderId(), event.getCancelReason().toOrderStatus());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_CANCELED_BY_RETURN, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(PaymentCanceledByReturnKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.updateStatus(event.getOrderId(), event.getCancelReason().toOrderStatus());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.PAYMENT_CANCELED_BY_ROLLBACK, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(PaymentCanceledByRollbackKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.updateStatusByRollback(event.getOrderId(), event.getCancelReason().toOrderStatus());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.USER_DELETED, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(UserDeletedKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.deleteOrder(event.getUserId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }

    @KafkaListener(topics = TopicNames.STOCK_REDIS_ROLLED_BACK, groupId = GroupIds.KAFKA_GROUP_ORDER)
    public void listen(StockRedisRolledBackKafkaEvent event) {
        if (processedEventService.isProcessed(event.getEventId())) return;

        try {
            orderService.onRedisRolledBack(event.getOrderId());
        } catch (Exception e) {
            eventFailureService.saveEventFailure(event, e);
        }
    }
}
