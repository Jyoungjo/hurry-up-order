package com.purchase.preorder.payment_service.api.external.pg.dto.nice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NicePaymentResponse {
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
    private Integer amount;
    private Integer balanceAmt;
    private String goodsName;
    private String mallReserved;
    private Boolean useEscrow;
    private String currency;
    private String channel;
    private String approveNo;
    private String buyerName;
    private String buyerTel;
    private String buyerEmail;
    private String receiptUrl;
    private String mallUserId;
    private Boolean issuedCashReceipt;
    private Coupon coupon;
    private Card card;
    private JsonNode vbank;          // null 허용 + 유연성 확보
    private JsonNode bank;
    private JsonNode cellphone;
    private List<Cancel> cancels;
    private JsonNode cashReceipts;
    private String messageSource;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coupon {
        private Integer couponAmt;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String cardCode;
        private String cardName;
        private String cardNum;
        private Integer cardQuota;
        private Boolean isInterestFree;
        private String cardType;
        private Boolean canPartCancel;
        private String acquCardCode;
        private String acquCardName;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancel {
        private String tid;
        private Integer amount;
        private String cancelledAt;
        private String reason;
        private String receiptUrl;
        private Integer couponAmt;
    }
}
