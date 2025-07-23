package com.purchase.preorder.payment_service.event;

import com.common.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceedEvent {
    private Long paymentId;
    private Long orderId;
    private String pgOrderId;
    private String pgName;
    private int totalAmount;
    private int feeAmount;
    private int settledAmount;
    private LocalDateTime soldAt;

    public static PaymentSucceedEvent from(Payment payment, int feeAmount) {
        return PaymentSucceedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .pgOrderId(payment.getPgOrderId())
                .pgName(payment.getPgName())
                .totalAmount(payment.getPaymentPrice())
                .feeAmount(feeAmount)
                .settledAmount(payment.getPaymentPrice() - feeAmount)
                .soldAt(LocalDateTime.now())
                .build();
    }
}
