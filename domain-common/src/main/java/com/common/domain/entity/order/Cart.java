package com.common.domain.entity.order;

import com.common.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CART")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CartItem> cartItems = new ArrayList<>();

    @Builder
    private Cart(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static Cart of(Long userId) {
        return Cart.builder()
                .userId(userId)
                .build();
    }

    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.assignCart(this);
    }
}