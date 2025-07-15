package com.purchase.preorder.wishlist;

import com.purchase.preorder.user_service.config.JpaConfig;
import com.purchase.preorder.user.User;
import com.purchase.preorder.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static com.purchase.preorder.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class WishlistRepositoryTest {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Wishlist wishlist() {
        return Wishlist.builder()
                .id(1L)
                .user(user)
                .wishlistItems(new ArrayList<>())
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
    @DisplayName("위시리스트 생성 성공")
    @Test
    void createWishlist() {
        // given
        Wishlist wishlist = wishlist();

        // when
        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        // then
        assertThat(savedWishlist.getId()).isEqualTo(wishlist.getId());
    }

    // READ
    @DisplayName("위시리스트 조회 성공")
    @Test
    void readMyWishlist() {
        // given
        Wishlist savedWishlist = wishlistRepository.save(wishlist());

        // when
        Optional<Wishlist> foundWishlist = wishlistRepository.findByUser(user);

        // then
        assertThat(foundWishlist).isPresent();
        assertThat(foundWishlist.get().getId()).isEqualTo(savedWishlist.getId());
    }
}
