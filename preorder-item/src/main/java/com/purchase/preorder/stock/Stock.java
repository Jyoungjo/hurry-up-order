package com.purchase.preorder.stock;

import com.purchase.preorder.common.BaseEntity;
import com.purchase.preorder.item.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_STOCK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_STOCK SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class Stock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;
    private Integer quantity;
    private LocalDateTime deletedAt;

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public static Stock of(Item item, int quantity) {
        return Stock.builder()
                .item(item)
                .quantity(quantity)
                .build();
    }
}
