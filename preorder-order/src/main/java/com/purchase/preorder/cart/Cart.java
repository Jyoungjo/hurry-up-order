package com.purchase.preorder.cart;

import com.purchase.preorder.cart_item.CartItem;
import com.purchase.preorder.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CART")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<CartItem> cartItems;

    public static Cart of(Long userId) {
        return Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>())
                .build();
    }
}
