package com.common.domain.entity;

import com.common.domain.common.BaseEntity;
import com.common.domain.common.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE TB_ORDER SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime orderDate;

    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> orderItemList = new ArrayList<>();

    @Builder
    private Order(Long userId, Integer totalPrice, LocalDateTime orderDate, OrderStatus status) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
        this.status = status != null ? status : OrderStatus.CREATED;
    }

    public static Order of(Long userId, Integer totalPrice) {
        return Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .build();
    }

    public void addOrderItem(OrderItem item) {
        this.orderItemList.add(item);
        item.assignOrder(this);
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}