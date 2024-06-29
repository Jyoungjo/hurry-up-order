package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.shipment.Shipment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_ORDER_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalSum;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shipment_id")
    private Shipment shipment;

    public static OrderItem of(Order order, Item item, Shipment shipment, Integer quantity) {
        return OrderItem.builder()
                .order(order)
                .item(item)
                .shipment(shipment)
                .quantity(quantity)
                .unitPrice(item.getPrice())
                .totalSum(quantity * item.getPrice())
                .build();
    }
}
