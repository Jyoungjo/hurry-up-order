package com.purchase.preorder.settlement_service.event.listener;

import com.common.event_common.domain_event_vo.payment.PaymentCanceledByCancelDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentDeletedDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentSucceedDomainEvent;
import com.purchase.preorder.settlement_service.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementDomainEventListener {
    private final SettlementService settlementService;
    private final SettlementDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentSucceedDomainEvent event) {
        helper.executeWithFailureHandling(event, () -> settlementService.create(event));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentCanceledByCancelDomainEvent event) {
        helper.executeWithFailureHandling(event, () -> settlementService.reverseSettlement(event.getPaymentId()));
    }

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentDeletedDomainEvent event) {
        helper.executeWithFailureHandling(event, () -> settlementService.delete(event.getPaymentId()));
    }
}
