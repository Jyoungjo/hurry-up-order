package com.common.domain.entity;

import com.common.domain.common.OrderItemStatus;
import com.common.domain.common.PaymentStatus;
import com.common.domain.common.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_ORDER_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalSum;

    @Column(name = "shipment_id")
    private Long shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatus orderItemStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus shipmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    public static OrderItem of(Long itemId, Integer quantity, Integer unitPrice, Long shipmentId) {
        return OrderItem.builder()
                .itemId(itemId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalSum(quantity * unitPrice)
                .shipmentId(shipmentId)
                .orderItemStatus(OrderItemStatus.ORDERED)
                .shipmentStatus(ShipmentStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void assignShipment(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void updateOrderItemStatus(OrderItemStatus status) {
        this.orderItemStatus = status;
    }

    public void updateShipmentStatus(ShipmentStatus status) {
        this.shipmentStatus = status;
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }
}
