package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.order.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ORDER_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_ORDER_ITEM SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
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
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime readyAt;
    private LocalDateTime shippingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime requestReturnAt;
    private LocalDateTime returnedAt;
    private LocalDateTime deletedAt;

    public static OrderItem of(Order order, Item item, Integer quantity) {
        return OrderItem.builder()
                .order(order)
                .item(item)
                .quantity(quantity)
                .unitPrice(item.getPrice())
                .status(OrderStatus.ACCEPTANCE)
                .build();
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            this.deliveredAt = LocalDateTime.now();
        } else if (orderStatus.equals(OrderStatus.REQUEST_RETURN)) {
            this.requestReturnAt = LocalDateTime.now();
        } else if (orderStatus.equals(OrderStatus.READY)) {
            this.readyAt = LocalDateTime.now();
        } else if (orderStatus.equals(OrderStatus.SHIPPING)) {
            this.shippingAt = LocalDateTime.now();
        }
    }
}
