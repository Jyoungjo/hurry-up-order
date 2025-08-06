package com.common.domain.entity.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PROCESSED_EVENT_LOG")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProcessedEventLog {
    @Id
    private String eventId;
    private LocalDateTime processedAt;

    public static ProcessedEventLog from(String eventId) {
        return ProcessedEventLog.builder()
                .eventId(eventId)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
