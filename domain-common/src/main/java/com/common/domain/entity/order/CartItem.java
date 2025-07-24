package com.common.domain.entity.order;

import com.common.domain.message.CartItemMessages;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "TB_CART_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "item_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private CartItem(Long id, Long itemId, Integer quantity, LocalDateTime createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public static CartItem of(Long itemId, Integer quantity) {
        return CartItem.builder()
                .itemId(itemId)
                .quantity(quantity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void assignCart(Cart cart) {
        this.cart = cart;
    }

    public void increaseBy(int amount) {
        this.quantity += amount;
    }

    public void decreaseBy(int amount) {
        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException(CartItemMessages.QUANTITY_CANNOT_BE_ZERO_OR_LESS);
        }
        this.quantity -= amount;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(CartItemMessages.QUANTITY_CANNOT_BE_NEGATIVE);
        }
        this.quantity = quantity;
    }
}