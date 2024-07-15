package com.purchase.preorder.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPaymentDto {
    private Long orderId;
    private Integer totalPrice;
}
