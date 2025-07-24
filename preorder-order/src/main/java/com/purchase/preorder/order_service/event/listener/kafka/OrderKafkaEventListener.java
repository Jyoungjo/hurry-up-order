package com.purchase.preorder.order_service.event.listener.kafka;

import com.common.domain.common.OrderStatus;
import com.common.kafka.constant.GroupIds;
import com.common.kafka.constant.TopicNames;
import com.common.kafka.event_vo.payment.*;
import com.common.kafka.event_vo.stock.StockDecreasedKafkaEvent;
import com.common.kafka.event_vo.stock.StockRedisRolledBackKafkaEvent;
import com.common.kafka.event_vo.user.UserDeletedKafkaEvent;
import com.common.kafka.fail.EventFailureService;
import com.common.kafka.listener.AbstractKafkaEventListener;
import com.common.kafka.log.ProcessedEventService;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        topics = TopicNames.ORDER_EVENTS,
        groupId = GroupIds.KAFKA_GROUP_ORDER
)
public class OrderKafkaEventListener extends AbstractKafkaEventListener {
    private final OrderService orderService;

    public OrderKafkaEventListener(ProcessedEventService processedEventService,
                                   EventFailureService eventFailureService,
                                   OrderService orderService) {
        super(processedEventService, eventFailureService);
        this.orderService = orderService;
    }

    @KafkaHandler
    public void handle(PaymentSucceedKafkaEvent e) {
        dispatch(e, ev -> orderService.onPaymentSucceed(ev.getOrderId()));
    }

    @KafkaHandler
    public void listen(PaymentFailureKafkaEvent e) {
        dispatch(e, ev -> orderService.onPaymentFailure(ev.getOrderId()));
    }

    @KafkaHandler
    public void listen(StockDecreasedKafkaEvent e) {
        dispatch(e, ev -> orderService.updateStatus(ev.getOrderId(), OrderStatus.PAID));
    }

    @KafkaHandler
    public void listen(PaymentCanceledByCancelKafkaEvent e) {
        dispatch(e, ev -> orderService.updateStatus(ev.getOrderId(), ev.getCancelReason().toOrderStatus()));
    }

    @KafkaHandler
    public void listen(PaymentCanceledByReturnKafkaEvent e) {
        dispatch(e, ev -> orderService.updateStatus(ev.getOrderId(), ev.getCancelReason().toOrderStatus()));
    }

    @KafkaHandler
    public void listen(PaymentCanceledByRollbackKafkaEvent e) {
        dispatch(e, ev -> orderService.onPaymentFailure(ev.getOrderId()));
    }

    @KafkaHandler
    public void listen(UserDeletedKafkaEvent e) {
        dispatch(e, ev -> orderService.deleteOrder(ev.getUserId()));
    }

    @KafkaHandler
    public void listen(StockRedisRolledBackKafkaEvent e) {
        dispatch(e, ev -> orderService.onRedisRolledBack(ev.getOrderId()));
    }
}
