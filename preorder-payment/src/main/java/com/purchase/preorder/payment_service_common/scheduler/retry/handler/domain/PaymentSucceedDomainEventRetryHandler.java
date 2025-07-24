package com.purchase.preorder.payment_service_common.scheduler.retry.handler.domain;

import com.common.event_common.domain_event_vo.DomainEventType;
import com.common.event_common.domain_event_vo.payment.PaymentSucceedDomainEvent;
import com.common.event_common.handler.EventRetryHandler;
import com.purchase.preorder.settlement_service.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSucceedDomainEventRetryHandler implements EventRetryHandler<PaymentSucceedDomainEvent> {

    private final SettlementService settlementService;

    @Override
    public String getEventType() {
        return DomainEventType.PAYMENT_SUCCEED.name();
    }

    @Override
    public void handle(PaymentSucceedDomainEvent event) throws Exception {
        settlementService.create(event);
    }

    @Override
    public Class<PaymentSucceedDomainEvent> getEventClass() {
        return PaymentSucceedDomainEvent.class;
    }
}
