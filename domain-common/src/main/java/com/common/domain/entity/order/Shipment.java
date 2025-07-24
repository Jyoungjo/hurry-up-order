package com.common.domain.entity.order;

import com.common.domain.common.BaseEntity;
import com.common.domain.common.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SHIPMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE TB_SHIPMENT SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private LocalDateTime deletedAt;

    public static Shipment of(Long orderItemId) {
        return Shipment.builder()
                .orderItemId(orderItemId)
                .status(ShipmentStatus.ACCEPTANCE)
                .build();
    }

    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }
}