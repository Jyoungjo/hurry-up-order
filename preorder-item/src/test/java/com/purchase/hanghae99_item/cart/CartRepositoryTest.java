package com.purchase.hanghae99_item.cart;

import com.purchase.hanghae99_item.config.JpaConfig;
import com.purchase.hanghae99_item.user.User;
import com.purchase.hanghae99_item.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static com.purchase.hanghae99_item.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
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
        assertThat(savedCart.getUser()).isEqualTo(cart.getUser());
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
