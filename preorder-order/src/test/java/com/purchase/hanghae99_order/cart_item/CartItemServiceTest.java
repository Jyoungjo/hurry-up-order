package com.purchase.hanghae99_order.cart_item;

import com.purchase.hanghae99_order.cart.Cart;
import com.purchase.hanghae99_order.cart.dto.ReqCartDto;
import com.purchase.hanghae99_order.item.Item;
import com.purchase.hanghae99_order.item.ItemService;
import com.purchase.hanghae99_core.exception.BusinessException;
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

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private CartItemService cartItemService;

    private CartItem cartItem;
    private Cart cart;
    private Item item;

    @BeforeEach
    void init() {
        item = Item.builder()
                .id(1L)
                .name("제품명")
                .description("제품에 대한 설명입니다.")
                .price(150000)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(null)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .item(item)
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
                .user(null)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();

        int originalQuantity = newCart.getCartItems().get(0).getQuantity();

        when(itemService.findItem(anyLong())).thenReturn(item);

        CartItem existCartItem = CartItem.builder()
                .id(1L)
                .item(item)
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

        when(itemService.findItem(anyLong())).thenReturn(item);

        // when
        cartItemService.createCartItem(cart, req);

        // then
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertThat(savedCartItem.getItem()).isEqualTo(item);
        assertThat(savedCartItem.getQuantity()).isEqualTo(req.getQuantity());
    }

    // CREATE
    @DisplayName("장바구니 - 물품 추가 기능 실패(존재하지 않는 상품)")
    @Test
    void createCartItemFailNotFoundItem() {
        // given
        ReqCartDto req = new ReqCartDto(
                1L, 50
        );

        when(itemService.findItem(anyLong())).thenThrow(new BusinessException(NOT_FOUND_ITEM));

        // when

        // then
        assertThatThrownBy(() -> cartItemService.createCartItem(cart, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
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
                .user(null)
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
                .user(null)
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
                .user(null)
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
                .item(item)
                .cart(null)
                .quantity(1)
                .build();

        Cart newCart = Cart.builder()
                .id(1L)
                .user(null)
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
                .user(null)
                .cartItems(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> cartItemService.decrementCartItem(newCart, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART_ITEM.getMessage());
    }
}
