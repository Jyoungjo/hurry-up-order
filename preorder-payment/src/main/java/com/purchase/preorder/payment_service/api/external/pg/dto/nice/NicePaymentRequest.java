package com.purchase.preorder.payment_service.api.external.pg.dto.nice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NicePaymentRequest {
    private String tid;
    private String orderId;
    private int amount;

    public static NicePaymentRequest of(String tid, String orderId, int amount) {
        return NicePaymentRequest.builder()
                .tid(tid)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
