package com.purchase.hanghae99_item.cart_item;

import com.purchase.hanghae99_item.cart.Cart;
import com.purchase.hanghae99_item.item.Item;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;
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

    public static CartItem of(Item item, Cart cart, Integer quantity) {
        return CartItem.builder()
                .item(item)
                .cart(cart)
                .quantity(quantity)
                .build();
    }
}
