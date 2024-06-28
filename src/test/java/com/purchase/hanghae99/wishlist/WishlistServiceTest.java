package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.common.AesUtils;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import com.purchase.hanghae99.wishlist_item.WishlistItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Optional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static com.purchase.hanghae99.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemService wishlistItemService;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private User user;
    private Wishlist wishlist;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .email("a3acfa0a0267531ddd493ead683a99ae")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password("asd1234!!")
                .build();

        wishlist = Wishlist.builder()
                .id(1L)
                .user(user)
                .wishlistItems(new ArrayList<>())
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");
    }

    // CREATE
    @DisplayName("위시리스트 추가 기능 성공")
    @Test
    void addItemToWishlist() throws Exception {
        // given
        Long itemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistItemService).createWishListItem(any(Wishlist.class), anyLong());

        // when
        wishlistService.addItemToWishList(authentication, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistItemService, times(1)).createWishListItem(wishlist, itemId);
    }

    // CREATE
    @DisplayName("위시리스트 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void addItemToWishlistFailNotFoundUser() {
        // given
        Long itemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.addItemToWishList(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("위시리스트 조회 성공")
    @Test
    void readMyWishlist() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));

        // when
        ResWishListDto res = wishlistService.readMyWishList(authentication);

        // then
        assertThat(res.getWishListId()).isEqualTo(wishlist.getId());
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 유저")
    @Test
    void readMyWishlistFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.readMyWishList(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 위시리스트")
    @Test
    void readMyWishlistFailNotFoundWishlist() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.readMyWishList(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 성공")
    @Test
    void removeItemFromWishlist() throws Exception {
        // given
        Long itemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistItemService).deleteWishListItem(any(Wishlist.class), anyLong());

        // when
        wishlistService.removeItemFromWishList(authentication, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistItemService, times(1)).deleteWishListItem(wishlist, itemId);
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 실패 - 존재하지 않는 유저")
    @Test
    void removeItemFromWishlistFailNotFoundUser() {
        // given
        Long itemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.removeItemFromWishList(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 실패 - 존재하지 않는 위시리스트")
    @Test
    void removeItemFromWishlistFailNotFoundWishlist() {
        // given
        Long itemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.removeItemFromWishList(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 성공")
    @Test
    void clearWishlist() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // when
        wishlistService.clearWishlist(authentication);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 유저")
    @Test
    void clearWishlistFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 위시리스트")
    @Test
    void clearWishlistFailNotFoundWishlist() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 이메일 불일치")
    @Test
    void clearWishlistFailNotMatch() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
