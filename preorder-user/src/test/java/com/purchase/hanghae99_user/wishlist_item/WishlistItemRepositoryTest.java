package com.purchase.hanghae99_user.wishlist_item;

import com.purchase.hanghae99_user.config.JpaConfig;
import com.purchase.hanghae99_user.item.Item;
import com.purchase.hanghae99_user.item.ItemRepository;
import com.purchase.hanghae99_user.wishlist.Wishlist;
import com.purchase.hanghae99_user.wishlist.WishlistRepository;
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
public class WishlistItemRepositoryTest {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Wishlist wishlist;
    private Item item;

    private WishlistItem wishlistItem() {
        return WishlistItem.builder()
                .id(1L)
                .item(item)
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

        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("제품명")
                .price(150000)
                .description("이 제품에 대한 설명 입니다.")
                .deletedAt(null)
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
        assertThat(savedWishlistItem.getItem()).isEqualTo(wishlistItem.getItem());
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
