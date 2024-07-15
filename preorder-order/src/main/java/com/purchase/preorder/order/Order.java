package com.purchase.preorder.order;

import com.purchase.preorder.common.BaseEntity;
import com.purchase.preorder.order_item.OrderItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Long userId;
    private LocalDateTime orderDate;
    private Integer totalPrice;
    private LocalDateTime deletedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderItem> orderItemList;

    public static Order of(Long userId, int totalPrice) {
        return Order.builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .orderItemList(new ArrayList<>())
                .build();
    }
}
