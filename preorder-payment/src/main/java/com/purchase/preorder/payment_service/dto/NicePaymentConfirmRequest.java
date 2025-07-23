package com.purchase.preorder.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NicePaymentConfirmRequest {
    private int amount;

    public static NicePaymentConfirmRequest of(int amount) {
        return NicePaymentConfirmRequest.builder()
                .amount(amount)
                .build();
    }
}
