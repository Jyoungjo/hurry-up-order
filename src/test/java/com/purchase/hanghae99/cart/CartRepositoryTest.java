package com.purchase.hanghae99.cart;

import com.purchase.hanghae99.config.JpaConfig;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Optional;

import static com.purchase.hanghae99.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
public class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Cart cart() {
        return Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .id(1L)
                .email("email1@gmail.com")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password("asd1234!!")
                .build());
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
        assertThat(savedCart.getId()).isEqualTo(cart.getId());
    }

    // READ ONE
    @DisplayName("장바구니 조회 성공")
    @Test
    void readCart() {
        // given
        Cart savedCart = cartRepository.save(cart());

        // when
        Optional<Cart> foundCart = cartRepository.findById(1L);

        // then
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo(savedCart.getId());
    }
}
