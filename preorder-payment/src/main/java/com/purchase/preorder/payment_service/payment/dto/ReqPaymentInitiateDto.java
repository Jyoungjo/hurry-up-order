package com.purchase.preorder.payment_service.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPaymentInitiateDto {
    private Long ordId;
    @NotBlank
    private String orderId;
    @Min(value = 1, message = "결제 금액은 1 이상이어야 합니다.")
    private int amount;
}
