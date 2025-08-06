package com.common.domain.entity.item;

import com.common.domain.common.BaseEntity;
import com.common.domain.message.StockMessages;
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
    private Long itemId;
    private Integer quantity;
    private LocalDateTime deletedAt;

    public void adjustQuantity(int delta) {
        int newQuantity = this.quantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException(StockMessages.INSUFFICIENT_STOCK);
        }
        this.quantity = newQuantity;
    }

    public static Stock of(Long itemId, int quantity) {
        return Stock.builder()
                .itemId(itemId)
                .quantity(quantity)
                .build();
    }
}