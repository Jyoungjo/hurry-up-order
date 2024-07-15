package com.purchase.preorder.order_item;

import com.purchase.preorder.client.response.ItemResponse;
import com.purchase.preorder.order.Order;
import com.purchase.preorder.shipment.Shipment;
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
    private Long itemId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalSum;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shipment_id")
    private Shipment shipment;

    public static OrderItem of(Order order, ItemResponse itemResponse, Shipment shipment, Integer quantity) {
        return OrderItem.builder()
                .order(order)
                .itemId(itemResponse.getId())
                .shipment(shipment)
                .quantity(quantity)
                .unitPrice(itemResponse.getPrice())
                .totalSum(quantity * itemResponse.getPrice())
                .build();
    }
}
