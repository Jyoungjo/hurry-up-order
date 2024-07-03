package com.purchase.hanghae99_item.cart_item;

import com.purchase.hanghae99_item.cart.Cart;
import com.purchase.hanghae99_item.cart.CartRepository;
import com.purchase.hanghae99_item.config.JpaConfig;
import com.purchase.hanghae99_item.item.Item;
import com.purchase.hanghae99_item.item.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Cart cart;
    private Item item;

    private CartItem cartItem() {
        return CartItem.builder()
                .id(1L)
                .item(item)
                .cart(cart)
                .quantity(500)
                .build();
    }

    @BeforeEach
    void init() {
        cart = cartRepository.save(Cart.builder()
                .id(1L)
                .user(null)
                .cartItems(new ArrayList<>())
                .build());

        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("제품명")
                .price(150000)
                .description("이 제품에 대한 설명 입니다.")
                .deletedAt(null)
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
