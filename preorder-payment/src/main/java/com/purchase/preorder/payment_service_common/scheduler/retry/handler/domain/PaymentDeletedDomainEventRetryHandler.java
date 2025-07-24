package com.purchase.preorder.payment_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.payment.PaymentDeletedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.settlement_service.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDeletedDomainEventRetryHandler implements EventRetryHandler<PaymentDeletedDomainEvent> {

    private final SettlementService settlementService;

    @Override
    public String getEventType() {
        return DomainEventType.PAYMENT_DELETED.name();
    }

    @Override
    public void handle(PaymentDeletedDomainEvent event) throws Exception {
        settlementService.delete(event.getPaymentId());
    }

    @Override
    public Class<PaymentDeletedDomainEvent> getEventClass() {
        return PaymentDeletedDomainEvent.class;
    }
}
