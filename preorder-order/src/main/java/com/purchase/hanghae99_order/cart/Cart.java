package com.purchase.hanghae99_order.cart;

import com.purchase.hanghae99_order.cart_item.CartItem;
import com.purchase.hanghae99_order.common.BaseEntity;
import com.purchase.hanghae99_order.user.User;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<CartItem> cartItems;

    public static Cart of(User user) {
        return Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }
}
