package com.purchase.preorder.payment_service.api.external.pg.dto;

import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentResponse;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentResponse;
import com.purchase.preorder.payment_service_common.util.DateTimeParser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Builder
public class PaymentCompletionData {
    private final String orderId;
    private final String pgSource;
    private final LocalDateTime requestedAt;
    private final LocalDateTime approvedAt;
    private final Integer amount;
    private final String transactionId;

    public static PaymentCompletionData mapFromTossResponse(TossPaymentResponse res) {
        return PaymentCompletionData.builder()
                .orderId(res.getOrderId())
                .pgSource(res.getMId())
                .requestedAt(OffsetDateTime.parse(res.getRequestedAt()).toLocalDateTime())
                .approvedAt(OffsetDateTime.parse(res.getApprovedAt()).toLocalDateTime())
                .amount(res.getTotalAmount())
                .transactionId(res.getPaymentKey())
                .build();
    }

    public static PaymentCompletionData mapFromNiceResponse(NicePaymentResponse res) {
        return PaymentCompletionData.builder()
                .orderId(res.getOrderId())
                .pgSource(res.getMessageSource())
                .requestedAt(DateTimeParser.parse(res.getEdiDate()))
                .approvedAt(DateTimeParser.parse(res.getPaidAt()))
                .amount(res.getAmount())
                .transactionId(res.getTid())
                .build();
    }
}
