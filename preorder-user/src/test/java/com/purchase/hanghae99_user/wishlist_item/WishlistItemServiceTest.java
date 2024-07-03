package com.purchase.hanghae99_user.wishlist_item;

import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_user.item.Item;
import com.purchase.hanghae99_user.item.ItemService;
import com.purchase.hanghae99_user.wishlist.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistItemServiceTest {
    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private WishlistItemService wishlistItemService;

    private WishlistItem wishlistItem;
    private Wishlist wishlist;
    private Item item;

    @BeforeEach
    void init() {
        item = Item.builder()
                .id(1L)
                .name("제품명")
                .description("제품에 대한 설명입니다.")
                .price(150000)
                .build();

        wishlist = Wishlist.builder()
                .id(1L)
                .user(null)
                .wishlistItems(new ArrayList<>())
                .build();

        wishlistItem = WishlistItem.builder()
                .id(1L)
                .item(item)
                .wishlist(wishlist)
                .build();
    }

    // CREATE
    @DisplayName("위시리스트 - 상품 추가 기능 성공")
    @Test
    void createWishlistItem() {
        // given
        Long itemId = 1L;

        when(itemService.findItem(anyLong())).thenReturn(item);

        // when
        wishlistItemService.createWishListItem(wishlist, itemId);

        // then
        ArgumentCaptor<WishlistItem> wishlistItemCaptor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepository, times(1)).save(wishlistItemCaptor.capture());

        WishlistItem savedWishlistItem = wishlistItemCaptor.getValue();

        assertThat(savedWishlistItem.getItem()).isEqualTo(item);
    }

    // CREATE
    @DisplayName("위시리스트 - 상품 추가 기능 실패(이미 존재하는 경우)")
    @Test
    void createWishlistItemFailAlreadyExist() {
        // given
        Long itemId = 1L;

        when(itemService.findItem(anyLong())).thenReturn(item);

        Wishlist newWishlist = Wishlist.builder()
                .id(1L)
                .user(null)
                .wishlistItems(new ArrayList<>(List.of(wishlistItem)))
                .build();

        // when

        // then
        assertThatThrownBy(() -> wishlistItemService.createWishListItem(newWishlist, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_EXISTS_ITEM.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 - 상품 삭제 기능 성공")
    @Test
    void deleteWishlistItem() {
        // given
        Long itemId = 1L;

        when(wishlistItemRepository.findByWishlistAndItemId(any(Wishlist.class), anyLong()))
                .thenReturn(Optional.of(wishlistItem));
        doNothing().when(wishlistItemRepository).delete(any(WishlistItem.class));

        // when
        wishlistItemService.deleteWishListItem(wishlist, itemId);

        // then
        assertThat(wishlistItemRepository.count()).isZero();
    }

    // DELETE
    @DisplayName("위시리스트 - 상품 삭제 기능 실패(이미 없는 경우)")
    @Test
    void deleteWishlistItemFailAlreadyNotExist() {
        // given
        Long itemId = 1L;

        when(wishlistItemRepository.findByWishlistAndItemId(any(Wishlist.class), anyLong()))
                .thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistItemService.deleteWishListItem(wishlist, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST_ITEM.getMessage());
    }
}
