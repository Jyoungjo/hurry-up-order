package com.purchase.hanghae99_order.cart;

import com.purchase.hanghae99_order.cart.dto.ReqCartDto;
import com.purchase.hanghae99_order.cart.dto.ResCartDto;
import com.purchase.hanghae99_order.cart_item.CartItemService;
import com.purchase.hanghae99_order.user.User;
import com.purchase.hanghae99_order.user.UserRepository;
import com.purchase.hanghae99_core.util.AesUtils;
import com.purchase.hanghae99_core.exception.BusinessException;
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

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static com.purchase.hanghae99_order.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;

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

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");
    }

    // CREATE
    @DisplayName("장바구니 추가 기능 성공")
    @Test
    void addItem() throws Exception {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).createCartItem(any(Cart.class), any(ReqCartDto.class));

        // when
        cartService.addItemToCart(authentication, req);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartItemService, times(1)).createCartItem(cart, req);
    }

    // CREATE
    @DisplayName("장바구니 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void addItemFailNotFound() {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.addItemToCart(authentication, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("장바구니 조회 기능 성공")
    @Test
    void readMyCart() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));

        // when
        ResCartDto res = cartService.readMyCart(authentication);

        // then
        assertThat(res.getCartId()).isEqualTo(cart.getId());
    }

    // READ
    @DisplayName("장바구니 조회 기능 실패 - 존재하지 않는 유저")
    @Test
    void readMyCartFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.readMyCart(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("장바구니 조회 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void readMyCartFailNotFoundCart() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.readMyCart(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 성공")
    @Test
    void incrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).incrementCartItem(any(Cart.class), anyLong());

        // when
        cartService.incrementCartItemQuantity(authentication, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartItemService, times(1)).incrementCartItem(cart, itemId);
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void incrementCartItemQuantityFailNotFoundUser() {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.incrementCartItemQuantity(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void incrementCartItemQuantityFailNotFoundCart() {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.incrementCartItemQuantity(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 성공")
    @Test
    void decrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).decrementCartItem(any(Cart.class), anyLong());

        // when
        cartService.decrementCartItemQuantity(authentication, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartItemService, times(1)).decrementCartItem(cart, itemId);
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 실패 - 존재하지 않는 유저")
    @Test
    void decrementCartItemQuantityFailNotFoundUser() {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.decrementCartItemQuantity(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void decrementCartItemQuantityFailNotFoundCart() {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.decrementCartItemQuantity(authentication, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 성공")
    @Test
    void clearCart() throws Exception {
        // given
        Long itemId = 1L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        cartService.clearCart(authentication);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, times(1)).save(cart);
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 존재하지 않는 유저")
    @Test
    void clearCartFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void clearCartFailNotFoundCart() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 이메일 불일치")
    @Test
    void clearCartFailNotMatch() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
