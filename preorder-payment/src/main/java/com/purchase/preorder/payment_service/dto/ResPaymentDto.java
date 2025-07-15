package com.purchase.preorder.payment_service.dto;

import com.purchase.preorder.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResPaymentDto {
    private Long paymentId;
    private Boolean isSuccess;

    public static ResPaymentDto fromEntity(Payment payment) {
        return ResPaymentDto.builder()
                .paymentId(payment.getId())
                .isSuccess(true)
                .build();
    }
}
