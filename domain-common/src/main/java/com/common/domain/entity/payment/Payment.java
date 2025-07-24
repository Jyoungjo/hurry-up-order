package com.common.domain.entity.payment;

import com.common.domain.common.BaseEntity;
import com.common.domain.common.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@SQLDelete(sql = "UPDATE TB_PAYMENT SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String pgOrderId;

    /** PG사 이름 */
    private String pgName;

    /** 결제 시도 일시 */
    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime failedAt;

    private LocalDateTime canceledAt;

    /** 결제 완료(성공) 일시 */
    private LocalDateTime completedDate;

    private Double paymentPrice;

    /** PG사 트랜잭션 ID (주문 추적용) */
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime deletedAt;

    // 생성 시점에 INITIATED 상태, retryCount = 0
    public static Payment of(Long orderId, double paymentPrice, String pgOrderId, String pgTransactionId) {
        return Payment.builder()
                .orderId(orderId)
                .paymentPrice(paymentPrice)
                .pgOrderId(pgOrderId)
                .pgTransactionId(pgTransactionId)
                .paymentStatus(PaymentStatus.INITIATED)
                .build();
    }

    /** 결제 성공 처리 */
    public void completePayment(String pgOrderId, String pgName, LocalDateTime requestedAt,
                                LocalDateTime approvedAt, double paymentPrice, String paymentKey) {
        this.pgOrderId = pgOrderId;
        this.pgName = pgName;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.paymentPrice = paymentPrice;
        this.pgTransactionId = paymentKey;
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
    }

    /** 결제 실패 처리(최종 실패) */
    public void failPayment(String pgOrderId, String pgTransactionId) {
        this.pgOrderId = pgOrderId;
        this.pgTransactionId = pgTransactionId;
        this.paymentStatus = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();
    }

    /** 결제 취소 처리 */
    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }
}

