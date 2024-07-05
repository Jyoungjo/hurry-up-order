package com.purchase.preorder.wishlist_item;

import com.purchase.preorder.config.JpaConfig;
import com.purchase.preorder.wishlist.Wishlist;
import com.purchase.preorder.wishlist.WishlistRepository;
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
public class WishlistItemRepositoryTest {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    private Wishlist wishlist;

    private WishlistItem wishlistItem() {
        return WishlistItem.builder()
                .id(1L)
                .itemId(1L)
                .wishlist(wishlist)
                .build();
    }

    @BeforeEach
    void init() {
        wishlist = wishlistRepository.save(Wishlist.builder()
                .id(1L)
                .user(null)
                .wishlistItems(new ArrayList<>())
                .build());
    }

    // CREATE
    @DisplayName("위시리스트 - 상품 생성 성공")
    @Test
    void createWishlistItem() {
        // given
        WishlistItem wishlistItem = wishlistItem();

        // when
        WishlistItem savedWishlistItem = wishlistItemRepository.save(wishlistItem);

        // then
        assertThat(savedWishlistItem.getItemId()).isEqualTo(wishlistItem.getItemId());
    }

    // DELETE
    @DisplayName("위시리스트 - 상품 삭제 성공")
    @Test
    void deleteWishlistItem() {
        // given
        WishlistItem savedWishlistItem = wishlistItemRepository.save(wishlistItem());

        // when
        wishlistItemRepository.delete(savedWishlistItem);

        // then
        assertThat(wishlistItemRepository.count()).isZero();
    }
}
