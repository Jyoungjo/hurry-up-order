package com.common.domain.repository.common;

import com.common.domain.entity.common.ProcessedEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventLog, String> {
}
