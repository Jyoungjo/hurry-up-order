package com.common.domain.entity;

import com.common.domain.common.EventFailureStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EVENT_FAILURE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Lob
    private String payload;

    private String errorMessage;

    private int retryCount;

    private int maxRetry;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventFailureStatus status;

    private boolean processed;

    private LocalDateTime createdAt;

    private LocalDateTime lastRetryAt;

    public static EventFailure of(String eventType, String payload, String errorMessage) {
        return EventFailure.builder()
                .eventType(eventType)
                .payload(payload)
                .errorMessage(errorMessage)
                .retryCount(0)
                .maxRetry(5)
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void success() {
        this.processed = true;
        this.lastRetryAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
        this.errorMessage = errorMessage;

        if (this.retryCount >= this.maxRetry) {
            this.status = EventFailureStatus.DEAD;
            this.processed = true;
        }
    }
}
