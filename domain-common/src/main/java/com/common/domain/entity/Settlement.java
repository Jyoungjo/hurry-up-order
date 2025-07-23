package com.common.domain.entity;

import com.common.domain.common.BaseEntity;
import com.common.domain.common.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_settlement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@SQLDelete(sql = "UPDATE TB_SETTLEMENT SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Settlement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;

    private Long orderId;

    private String pgOrderId;

    private String pgName;

    private int totalAmount;

    private int feeAmount;

    private int settledAmount;

    private LocalDateTime soldAt;

    private LocalDateTime settledAt;

    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    public static Settlement of(Long paymentId, Long orderId, String pgOrderId, String pgName,
                                int totalAmount, int feeAmount, int settledAmount, LocalDateTime soldAt) {
        return Settlement.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .pgOrderId(pgOrderId)
                .pgName(pgName)
                .totalAmount(totalAmount)
                .feeAmount(feeAmount)
                .settledAmount(settledAmount)
                .soldAt(soldAt)
                .status(SettlementStatus.PENDING)
                .build();
    }

    public void settle(long totalAmount, long feeAmount, long settledAmount) {
        this.status = SettlementStatus.COMPLETED;
        this.totalAmount = (int) totalAmount;
        this.feeAmount = (int) feeAmount;
        this.settledAmount = (int) settledAmount;
        this.settledAt = LocalDateTime.now();
    }
}
