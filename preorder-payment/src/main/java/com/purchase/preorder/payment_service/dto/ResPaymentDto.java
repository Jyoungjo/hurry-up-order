package com.purchase.preorder.payment_service.dto;

import com.common.domain.common.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResPaymentDto {
    private Long paymentId;
    private Long orderId;
    private int totalPrice;
    private PaymentStatus status;

    public static ResPaymentDto of(Long paymentId, Long orderId, int totalPrice, PaymentStatus status) {
        return ResPaymentDto.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .totalPrice(totalPrice)
                .status(status)
                .build();
    }
}
