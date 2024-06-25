package com.purchase.hanghae99.order;

import com.purchase.hanghae99.common.BaseEntity;
import com.purchase.hanghae99.order_item.OrderItem;
import com.purchase.hanghae99.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_ORDER SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    private LocalDateTime orderDate;
    private Integer totalSum;
    private LocalDateTime deletedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderItem> orderItemList;

    public static Order of(User user) {
        return Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalSum(null)
                .build();
    }

    public void calTotalSum() {
        this.totalSum = orderItemList
                .stream()
                .mapToInt(orderItem -> orderItem.getQuantity() * orderItem.getUnitPrice())
                .sum();
    }
}
