package com.purchase.preorder.order_service.event.listener.domain;

import com.common.event_common.domain_event_vo.order.OrderDeletedDomainEvent;
import com.common.event_common.domain_event_vo.order.OrderPaidDomainEvent;
import com.common.event_common.domain_event_vo.order.OrderPaymentFailedDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentCancelRequestedByReturnDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentCanceledDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentCreatedDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentReturnedDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentStatusChangedDomainEvent;
import com.common.event_common.domain_event_vo.stock.StockRollbackRequestedDomainEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDomainEventListener {

    private final OrderService orderService;
    private final OrderDomainEventListenHelper helper;

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderPaidDomainEvent domainEvent) {
        helper.executeOnlyPublishKafkaEvent(domainEvent);
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderPaymentFailedDomainEvent domainEvent) {
        helper.executeOnlyPublishKafkaEvent(domainEvent);
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(ShipmentCreatedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> orderService.assignShipments(event.getOrderId(), event.getShipmentMap()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(ShipmentStatusChangedDomainEvent domainEvent) {
        helper.executeWithFailureHandling(
                domainEvent,
                () -> orderService.updateStatusByShipment(domainEvent.getShipmentId(), domainEvent.getStatus())
        );
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(ShipmentCanceledDomainEvent domainEvent) {
        helper.executeWithFailureHandling(
                domainEvent,
                () -> orderService.onShipmentCancel(domainEvent.getOrderId(), domainEvent.getCancelReason())
        );
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(ShipmentReturnedDomainEvent domainEvent) {
        helper.executeWithFailureHandling(
                domainEvent,
                () -> orderService.onShipmentReturn(domainEvent.getShipmentId(), domainEvent.getOrderItemId(), domainEvent.getCancelReason())
        );
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentCancelRequestedByReturnDomainEvent domainEvent) {
        helper.executeOnlyPublishKafkaEvent(domainEvent);
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderDeletedDomainEvent domainEvent) {
        helper.executeOnlyPublishKafkaEvent(domainEvent);
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(StockRollbackRequestedDomainEvent domainEvent) {
        helper.executeOnlyPublishKafkaEvent(domainEvent);
    }
}
