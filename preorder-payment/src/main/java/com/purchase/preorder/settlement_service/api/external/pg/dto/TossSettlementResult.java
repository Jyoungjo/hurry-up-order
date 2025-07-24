package com.purchase.preorder.settlement_service.api.external.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossSettlementResult {
    private String mId;
    private String paymentKey;
    private String transactionKey;
    private String orderId;
    private String currency;
    private String method;
    private Long amount;
    private Long interestFee;
    private List<FeeDetail> fees;
    private Long supplyAmount;
    private Long vat;
    private Long payOutAmount;
    private ZonedDateTime approvedAt;
    private LocalDate soldDate;
    private LocalDate paidOutDate;
    private CardInfo card;
    private VirtualAccount virtualAccount;
    private Transfer transfer;
    private MobilePhone mobilePhone;
    private GiftCertificate giftCertificate;
    private EasyPay easyPay;
    private CancelInfo cancel;
    private String settlementStatus;
    private String receiptUrl;
    private Long fee;

    @Getter
    public static class FeeDetail {
        private String type;
        private Long fee;
    }

    @Getter
    public static class CardInfo {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private Boolean isInterestFree;
        private String interestPayer;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Long amount;
    }

    @Getter
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
        public static class RefundReceiveAccount {
            private String bankCode;
            private String accountNumber;
            private String holderName;
        }
    }

    @Getter
    public static class Transfer {
        private String bankCode;
        private String settlementStatus;
    }

    @Getter
    public static class MobilePhone {
        private String customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;
    }

    @Getter
    public static class GiftCertificate {
        private String approveNo;
        private String settlementStatus;
    }

    @Getter
    public static class EasyPay {
        private String provider;
        private Long amount;
        private Long discountAmount;
    }

    @Getter
    public static class CancelInfo {
        private String transactionKey;
        private String cancelReason;
        private Integer taxExemptionAmount;
        private ZonedDateTime canceledAt;
        private Integer transferDiscountAmount;
        private Integer easyPayDiscountAmount;
        private String receiptKey;
        private Long cancelAmount;
        private Long taxFreeAmount;
        private Long refundableAmount;
        private String cancelStatus;
        private String cancelRequestId;
    }
}
