package com.purchase.preorder.cart_item;

import com.purchase.preorder.cart.Cart;
import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart_item.CartItemService;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order_service.api.internal.ItemClient;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_CART_ITEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private CartItemService cartItemService;

    private CartItem cartItem;
    private Cart cart;
    private ItemResponse item;

    @BeforeEach
    void init() {
        item = new ItemResponse(
                1L, "제품명", "제품 설명",
                10000, LocalDateTime.now(), false, null
        );

        cart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .itemId(1L)
                .cart(cart)
                .quantity(50)
                .build();
    }

    // CREATE
    @DisplayName("장바구니 - 물품 추가 기능 성공(수량이 존재하면 개수 증가)")
    @Test
    void createCartItemExistItem() {
        // given
        ReqCartDto req = new ReqCartDto(
                1L, 50
        );

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();

        int originalQuantity = newCart.getCartItems().get(0).getQuantity();

        CartItem existCartItem = CartItem.builder()
                .id(1L)
                .itemId(1L)
                .cart(newCart)
                .quantity(originalQuantity + 50)
                .build();

        when(cartItemRepository.save(any(CartItem.class))).thenReturn(existCartItem);

        // when
        cartItemService.createCartItem(newCart, req);

        // then
        assertThat(originalQuantity).isNotEqualTo(existCartItem.getQuantity());
    }

    // CREATE
    @DisplayName("장바구니 - 물품 추가 기능 성공(수량이 존재하지 않으면 새 객체 생성)")
    @Test
    void createCartItemNotExistItem() {
        // given
        ReqCartDto req = new ReqCartDto(
                1L, 50
        );

        // when
        cartItemService.createCartItem(cart, req);

        // then
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertThat(savedCartItem.getItemId()).isEqualTo(item.getId());
        assertThat(savedCartItem.getQuantity()).isEqualTo(req.getQuantity());
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 증가 기능 성공")
    @Test
    void incrementCartItem() {
        // given
        Long itemId = 1L;
        int originalQuantity = cartItem.getQuantity();

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();

        // when
        cartItemService.incrementCartItem(newCart, itemId);

        // then
        assertThat(cartItem.getQuantity()).isEqualTo(originalQuantity + 1);
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 증가 기능 실패(장바구니에 존재하지 않는 물품)")
    @Test
    void incrementCartItemFailNotExist() {
        // given
        Long itemId = 1L;

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> cartItemService.incrementCartItem(newCart, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART_ITEM.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 감소 기능 성공(계산 후 수량이 0 초과)")
    @Test
    void decrementCartItem() {
        // given
        Long itemId = 1L;
        int originalQuantity = cartItem.getQuantity();

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();

        // when
        cartItemService.decrementCartItem(newCart, itemId);

        // then
        assertThat(cartItem.getQuantity()).isEqualTo(originalQuantity - 1);
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 감소 기능 성공(계산 후 수량이 0 이하)")
    @Test
    void decrementCartItemAndDelete() {
        // given
        Long itemId = 1L;

        CartItem newCartItem = CartItem.builder()
                .id(1L)
                .itemId(1L)
                .cart(null)
                .quantity(1)
                .build();

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>(List.of(newCartItem)))
                .build();

        // when
        cartItemService.decrementCartItem(newCart, itemId);

        // then
        verify(cartItemRepository, times(1)).delete(newCartItem);
    }

    // UPDATE
    @DisplayName("장바구니 - 물품 수량 감소 기능 실패 - 장바구니에 존재하지 않는 물품")
    @Test
    void decrementCartItemFailNotExist() {
        // given
        Long itemId = 1L;

        Cart newCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> cartItemService.decrementCartItem(newCart, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART_ITEM.getMessage());
    }
}
