package com.common.domain.entity.common;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_OUTBOX")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String _type;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private boolean dlq;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void markPublished() {
        this.published = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
        if (this.retryCount >= 3) markDlq();
    }

    public void markDlq() {
        this.dlq = true;
        this.updatedAt = LocalDateTime.now();
    }
}
