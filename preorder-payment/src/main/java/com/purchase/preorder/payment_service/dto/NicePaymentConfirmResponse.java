package com.purchase.preorder.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NicePaymentConfirmResponse {
    private String resultCode;
    private String resultMsg;
    private String tid;
    private String cancelledTid;
    private String orderId;
    private String ediDate;
    private String signature;
    private String status;
    private String paidAt;
    private String failedAt;
    private String cancelledAt;
    private String payMethod;
    private int amount;
    private int balanceAmt;
    private String goodsName;
    private String mallReserved;
    private boolean useEscrow;
    private String currency;
    private String channel;
    private String approveNo;
    private String buyerName;
    private String buyerTel;
    private String buyerEmail;
    private String receiptUrl;
    private String mallUserId;
    private boolean issuedCashReceipt;
    private Coupon coupon;
    private Card card;
    private JsonNode vbank;          // null 허용 + 유연성 확보
    private JsonNode bank;
    private JsonNode cellphone;
    private JsonNode cancels;
    private JsonNode cashReceipts;
    private String messageSource;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coupon {
        private int couponAmt;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String cardCode;
        private String cardName;
        private String cardNum;
        private int cardQuota;
        private boolean isInterestFree;
        private String cardType;
        private boolean canPartCancel;
        private String acquCardCode;
        private String acquCardName;
    }
}
