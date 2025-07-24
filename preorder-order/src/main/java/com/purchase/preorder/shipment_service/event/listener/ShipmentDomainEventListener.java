package com.purchase.preorder.shipment_service.event.listener;

import com.common.event_common.domain_event_vo.order.*;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentDomainEventListener {

    private final ShipmentService shipmentService;
    private final ShipmentDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderCompletedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.createShipments(event.getOrderItemIds(), event.getOrderId()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderCancelRequestedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.cancelShipments(event.getOrderId(), event.getOrderItemIds()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderReturnRequestedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.requestReturnShipments(event.getOrderId(), event.getOrderItemIds()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderDeletedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.delete(event.getOrderItemIds()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderPaymentFailedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.deleteAllByOrderItemId(event.getOrderItemIds()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderCompensationCompletedDomainEvent event) {
        helper.executeWithFailureHandling(event,
                () -> shipmentService.deleteAllByOrderItemId(event.getOrderItemIds()));
    }
}
