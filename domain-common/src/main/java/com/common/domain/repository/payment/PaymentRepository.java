package com.common.domain.repository.payment;

import com.common.domain.entity.payment.Payment;
import com.common.domain.entity.payment.PaymentIdOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByPgOrderId(String pgOrderId);
    Optional<PaymentIdOnly> findPaymentIdOnlyByOrderId(Long orderId);

    @Modifying
    @Query("UPDATE Payment p SET p.deletedAt = :deletedAt WHERE p.orderId = :orderId")
    void deleteByOrderId(@Param("deletedAt") LocalDateTime deletedAt, @Param("orderId") Long orderId);
}