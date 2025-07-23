package com.purchase.preorder.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPaymentConfirmResponse {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    @JsonProperty("mId")
    private String mId;
    private String currency;
    private String method;
    private int totalAmount;
    private int balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private boolean useEscrow;
    private String lastTransactionKey;
    private String suppliedAmount;
    private int vat;
    private boolean cultureExpense;
    private int taxFreeAmount;
    private int taxExemptionAmount;
    private boolean isPartialCancelable;
    private PaymentCardDto card;
    private String country;


    @Getter
    public static class PaymentCardDto {
        private int amount;
        private String issuerCode;
        private String number;
        private int installmentPlanMonths;
        private String approveNo;
        private boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private boolean isInterestFree;
    }
}
