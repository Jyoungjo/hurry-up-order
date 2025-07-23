package com.common.domain.repository;

import com.common.domain.common.SettlementStatus;
import com.common.domain.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByStatusAndSettledAtIsNullAndSoldAtLessThanEqual(SettlementStatus status, LocalDateTime before);
}
