package com.purchase.preorder.payment_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.payment.PaymentCanceledByCancelDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.settlement_service.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCanceledDomainEventRetryHandler implements EventRetryHandler<PaymentCanceledByCancelDomainEvent> {

    private final SettlementService settlementService;

    @Override
    public String getEventType() {
        return DomainEventType.PAYMENT_CANCELED.name();
    }

    @Override
    public void handle(PaymentCanceledByCancelDomainEvent event) throws Exception {
        settlementService.reverseSettlement(event.getPaymentId());
    }

    @Override
    public Class<PaymentCanceledByCancelDomainEvent> getEventClass() {
        return PaymentCanceledByCancelDomainEvent.class;
    }
}
