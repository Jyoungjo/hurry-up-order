package com.purchase.preorder.payment_service.api.external.pg.dto.toss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TossPaymentResponse {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;

    @JsonProperty("mId")
    private String mId;

    private String currency;
    private String method;
    private Integer totalAmount;
    private Integer balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private Boolean useEscrow;
    private String lastTransactionKey;
    private Integer suppliedAmount;
    private Integer vat;
    private Boolean cultureExpense;
    private Integer taxFreeAmount;
    private Integer taxExemptionAmount;
    private List<Cancel> cancels;
    private Boolean isPartialCancelable;
    private Card card;
    private VirtualAccount virtualAccount;
    private String secret;
    private MobilePhone mobilePhone;
    private GiftCertificate giftCertificate;
    private Transfer transfer;
    private Object metadata;
    private Receipt receipt;
    private Checkout checkout;
    private EasyPay easyPay;
    private String country;
    private Failure failure;
    private Object cashReceipt;
    private Object cashReceipts;
    private Discount discount;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancel {
        private Integer cancelAmount;
        private String cancelReason;
        private Integer taxFreeAmount;
        private Integer taxExemptionAmount;
        private Integer refundableAmount;
        private Integer transferDiscountAmount;
        private Integer easyPayDiscountAmount;
        private String canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private Integer amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Boolean isInterestFree;
        private String interestPayer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private String dueDate;
        private String refundStatus;
        private Boolean expired;
        private RefundReceiveAccount refundReceiveAccount;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RefundReceiveAccount {
            private String bankCode;
            private String accountNumber;
            private String holderName;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobilePhone {
        private String customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftCertificate {
        private String approveNo;
        private String settlementStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transfer {
        private String bankCode;
        private String settlementStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Receipt {
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Checkout {
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EasyPay {
        private String provider;
        private Integer amount;
        private Integer discountAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Failure {
        private String code;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private Integer discount;
    }
}
