package com.purchase.hanghae99.order_items;

import com.purchase.hanghae99.common.BaseEntity;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
}
