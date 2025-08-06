package com.purchase.preorder.cart;

import com.purchase.preorder.order_service_common.config.JpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;

    private Cart cart() {
        return Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build();
    }

    // CREATE
    @DisplayName("장바구니 생성 성공")
    @Test
    void createCart() {
        // given
        Cart cart = cart();

        // when
        Cart savedCart = cartRepository.save(cart);

        // then
        assertThat(savedCart.getUserId()).isEqualTo(cart.getUserId());
    }

    // READ ONE
    @DisplayName("장바구니 조회 성공")
    @Test
    void readCart() {
        // given
        Cart savedCart = cartRepository.save(cart());

        // when
        Optional<Cart> foundCart = cartRepository.findById(savedCart.getId());

        // then
        Assertions.assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo(savedCart.getId());
    }
}
