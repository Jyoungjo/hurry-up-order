package com.common.domain.entity;

import com.common.domain.common.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SHIPMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private Shipment(Long orderItemId, ShipmentStatus status, LocalDateTime createdAt) {
        this.orderItemId = orderItemId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Shipment of(Long orderItemId) {
        return Shipment.builder()
                .orderItemId(orderItemId)
                .status(ShipmentStatus.ACCEPTANCE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
        this.createdAt = LocalDateTime.now();
    }
}