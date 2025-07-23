package com.purchase.preorder.payment_service.event;

import com.common.domain.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishPaymentSucceedEventAfterCommit(Payment payment) {
        // TODO PG사 수수료에 따라 변경 필요 -> 우선 고정값 3.3% 로 계산
        final int feeAmount = (int) (payment.getPaymentPrice() * 0.033);
        log.info("결제 완료 이벤트 발행 시도");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publisher.publishEvent(PaymentSucceedEvent.from(payment, feeAmount));
            }
        });
        log.info("결제 완료 이벤트 발행 완료");
    }
}
