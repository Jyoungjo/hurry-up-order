package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.common.BaseEntity;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order.dto.ReqOrderItemDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ORDER_ITEMS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_ORDER_ITEMS SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class OrderItem extends BaseEntity {
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
    private OrderStatus status;
    private LocalDateTime deletedAt;

    public static OrderItem of(Order order, Item item, ReqOrderItemDto dto) {
        return OrderItem.builder()
                .order(order)
                .item(item)
                .quantity(dto.getItemCount())
                .unitPrice(item.getPrice())
                .status(OrderStatus.ACCEPTANCE)
                .build();
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}
