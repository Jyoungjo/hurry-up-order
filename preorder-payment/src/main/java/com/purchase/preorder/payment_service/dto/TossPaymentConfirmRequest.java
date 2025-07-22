package com.purchase.preorder.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}
