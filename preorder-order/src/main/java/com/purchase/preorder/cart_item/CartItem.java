package com.purchase.preorder.cart_item;

import com.purchase.preorder.cart.Cart;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CART_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;
    private Long itemId;
    private Integer quantity;
    private LocalDateTime createdAt;

    public void updateQuantity(Integer quantity) {
        this.quantity += quantity;
    }

    public void incrementQuantity() {
        this.quantity += 1;
    }

    public void decrementQuantity() {
        this.quantity -= 1;
    }

    public static CartItem of(Long itemId, Cart cart, Integer quantity) {
        return CartItem.builder()
                .itemId(itemId)
                .cart(cart)
                .quantity(quantity)
                .build();
    }
}
