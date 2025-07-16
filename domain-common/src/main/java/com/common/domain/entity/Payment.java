package com.common.domain.entity;

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
    private LocalDateTime paymentDate;
    private Integer paymentPrice;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime deletedAt;

    public static Payment of(Long orderId, int price) {
        return Payment.builder()
                .orderId(orderId)
                .paymentPrice(price)
                .paymentStatus(PaymentStatus.INITIATED)
                .build();
    }

    public void completePayment() {
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.COMPLETED;
    }
}
