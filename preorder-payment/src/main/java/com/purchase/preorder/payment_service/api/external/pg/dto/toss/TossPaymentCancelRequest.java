package com.purchase.preorder.payment_service.api.external.pg.dto.toss;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPaymentCancelRequest {
    @NotBlank
    @Size(max = 200)
    private String cancelReason;

    private int cancelAmount;

    private int taxFreeAmount;

    private String currency;

    private RefundReceiveAccount refundReceiveAccount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundReceiveAccount {

        @NotBlank
        private String bank; // 은행 코드

        @NotBlank
        @Size(max = 20)
        private String accountNumber;

        @NotBlank
        @Size(max = 60)
        private String holderName;
    }

    public static TossPaymentCancelRequestBuilder withReason(String cancelReason) {
        return TossPaymentCancelRequest.builder().cancelReason(cancelReason);
    }
}
