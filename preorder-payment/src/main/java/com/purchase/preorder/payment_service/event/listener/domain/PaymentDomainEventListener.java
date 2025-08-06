package com.purchase.preorder.payment_service.event.listener.domain;

import com.common.event_common.domain_event_vo.payment.PaymentCanceledByCancelDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentFailureDomainEvent;
import com.common.event_common.domain_event_vo.payment.PaymentSucceedDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDomainEventListener {

    private final PaymentDomainEventListenHelper helper;

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentSucceedDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentFailureDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }

    @Async(value = "kafkaPublishTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(PaymentCanceledByCancelDomainEvent event) {
        helper.executeOnlyPublishKafkaEvent(event);
    }
}
