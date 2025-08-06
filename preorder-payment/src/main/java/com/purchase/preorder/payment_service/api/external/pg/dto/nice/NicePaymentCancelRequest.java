package com.purchase.preorder.payment_service.api.external.pg.dto.nice;

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
public class NicePaymentCancelRequest {
    @NotBlank
    @Size(max = 50)
    private String reason;

    @NotBlank
    private String orderId;

    private int cancelAmt;

    private String mallReserved;

    private String ediDate;

    private String signDate;

    private String returnCharSet;

    private int taxFreeAmt;

    private String refundAccount;
    private String refundBankCode;
    private String refundHolder;

    public static NicePaymentCancelRequest.NicePaymentCancelRequestBuilder with(String reason, String orderId) {
        return NicePaymentCancelRequest.builder().reason(reason).orderId(orderId);
    }
}
