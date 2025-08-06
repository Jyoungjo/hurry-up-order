package com.common.event_common.mapper;

import com.common.domain.entity.payment.Payment;
import com.common.event_common.domain_event_vo.payment.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentDomainEventMapper {

    public PaymentSucceedDomainEvent toPaymentSucceedEvent(Payment payment) {
        // TODO PG사 수수료에 따라 변경 필요 -> 우선 고정값 3.3% 로 계산
        final int feeAmount = (int) (payment.getPaymentPrice() * 0.033);

        return PaymentSucceedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .pgOrderId(payment.getPgOrderId())
                .pgName(payment.getPgName())
                .totalAmount(payment.getPaymentPrice().intValue())
                .feeAmount(feeAmount)
                .settledAmount(payment.getPaymentPrice().intValue() - feeAmount)
                .soldAt(LocalDateTime.now())
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentFailureDomainEvent toPaymentFailureEvent(Long orderId) {
        return PaymentFailureDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCanceledByCancelDomainEvent toPaymentCanceledByCancelEvent(Long paymentId, Long orderId, String cancelReason) {
        return PaymentCanceledByCancelDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(paymentId)
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCanceledByReturnDomainEvent toPaymentCanceledByReturnEvent(Long shipmentId, Long paymentId, Long orderId, String cancelReason) {
        return PaymentCanceledByReturnDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(paymentId)
                .shipmentId(shipmentId)
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentCanceledByRollbackDomainEvent toPaymentCanceledByRollbackEvent(Long paymentId, Long orderId, String cancelReason) {
        return PaymentCanceledByRollbackDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(paymentId)
                .orderId(orderId)
                .cancelReason(cancelReason)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public PaymentDeletedDomainEvent toPaymentDeletedEvent(Long paymentId) {
        return PaymentDeletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(paymentId)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
