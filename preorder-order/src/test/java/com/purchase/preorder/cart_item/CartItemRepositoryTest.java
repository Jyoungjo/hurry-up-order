package com.purchase.preorder.cart_item;

import com.purchase.preorder.cart.Cart;
import com.purchase.preorder.cart.CartRepository;
import com.purchase.preorder.order_service.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    private Cart cart;

    private CartItem cartItem() {
        return CartItem.builder()
                .id(1L)
                .itemId(1L)
                .cart(cart)
                .quantity(500)
                .build();
    }

    @BeforeEach
    void init() {
        cart = cartRepository.save(Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build());
    }

    // CREATE
    @DisplayName("장바구니 - 물품 추가 성공")
    @Test
    void createCartItem() {
        // given
        CartItem cartItem = cartItem();

        // when
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // then
        assertThat(savedCartItem.getQuantity()).isEqualTo(cartItem.getQuantity());
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 증가 성공")
    @Test
    void updateCartItemIncrease() {
        // given
        CartItem savedCartItem = cartItemRepository.save(cartItem());

        int quantity = savedCartItem.getQuantity();

        // when
        savedCartItem.incrementQuantity();
        CartItem updatedCartItem = cartItemRepository.save(savedCartItem);

        // then
        assertThat(updatedCartItem.getQuantity()).isNotEqualTo(quantity);
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 감소 성공")
    @Test
    void updateCartItemDecrease() {
        // given
        CartItem savedCartItem = cartItemRepository.save(cartItem());

        int quantity = savedCartItem.getQuantity();

        // when
        savedCartItem.decrementQuantity();
        CartItem updatedCartItem = cartItemRepository.save(savedCartItem);

        // then
        assertThat(updatedCartItem.getQuantity()).isNotEqualTo(quantity);
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 삭제 성공")
    @Test
    void deleteCartItem() {
        // given
        CartItem savedCartItem = cartItemRepository.save(cartItem());

        // when
        cartItemRepository.delete(savedCartItem);

        // then
        assertThat(cartItemRepository.count()).isZero();
    }
}
