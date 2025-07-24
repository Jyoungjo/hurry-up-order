package com.common.domain.repository.common;

import com.common.domain.entity.common.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    Optional<OutboxMessage> findByEventId(String eventId);
    List<OutboxMessage> findTop100ByPublishedFalseAndDlqFalseOrderByCreatedAtAsc();
}
