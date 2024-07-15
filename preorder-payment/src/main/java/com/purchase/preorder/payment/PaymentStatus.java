package com.purchase.preorder.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    INITIATED("결제 시도"),
    COMPLETED("결제 완료");
    private final String status;
}
