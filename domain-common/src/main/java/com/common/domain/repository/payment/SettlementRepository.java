package com.common.domain.repository.payment;

import com.common.domain.common.SettlementStatus;
import com.common.domain.entity.payment.PendingSettlement;
import com.common.domain.entity.payment.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByPaymentId(Long paymentId);

    @Modifying
    @Query("UPDATE Settlement s SET s.deletedAt = :deletedAt WHERE s.paymentId = :paymentId")
    void deleteAllByPaymentIdIn(@Param("deletedAt") LocalDateTime deletedAt, @Param("paymentId") Long paymentId);

    @Modifying
    @Query("UPDATE Settlement s SET s.status = :status WHERE s.paymentId = :paymentId")
    void updateSettlementStatus(@Param("status") SettlementStatus status, @Param("paymentId") Long paymentId);

    List<PendingSettlement> findByStatusAndPgNameAndSettledAtIsNullAndSoldAtLessThanEqual(
            SettlementStatus status,
            String pgName,
            LocalDateTime cutoffDate
    );
}
