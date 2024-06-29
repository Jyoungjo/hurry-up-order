package com.purchase.hanghae99.shipment;

import com.purchase.hanghae99.order_item.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SHIPMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "shipment", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private OrderItem orderItem;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
    private LocalDateTime createdAt;

    public static Shipment of() {
        return Shipment.builder()
                .status(ShipmentStatus.ACCEPTANCE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateStatus(ShipmentStatus status) {
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
