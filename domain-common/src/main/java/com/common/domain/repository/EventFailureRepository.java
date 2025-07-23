package com.common.domain.repository;

import com.common.domain.entity.EventFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFailureRepository extends JpaRepository<EventFailure, Long> {
    List<EventFailure> findTop100ByProcessedFalseAndRetryCountLessThan(int cnt);
}
