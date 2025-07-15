package com.purchase.preorder.order_service.cart_item;

import com.purchase.preorder.cart.Cart;
import com.purchase.preorder.order_service.cart.dto.ReqCartDto;
import com.purchase.preorder.order_service.cart.dto.ResCartItemDto;
import com.purchase.preorder.order_service.client.ItemClient;
import com.purchase.preorder.order_service.client.response.ItemResponse;
import com.purchase.preorder.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_CART_ITEM;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemClient itemClient;

    public void createCartItem(Cart cart, ReqCartDto req) {
        cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItemId().equals(req.getItemId()))
                .findFirst()
                .ifPresentOrElse(
                        cartItem -> {
                            cartItem.updateQuantity(req.getQuantity());
                            cartItemRepository.save(cartItem);
                        },
                        () -> cartItemRepository.save(CartItem.of(req.getItemId(), cart, req.getQuantity())));
    }

    public void incrementCartItem(Cart cart, Long itemId) {
        cart.getCartItems().stream()
                .filter(c -> c.getItemId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    cartItem.incrementQuantity();
                    cartItemRepository.save(cartItem);
                }, () -> {
                    throw new BusinessException(NOT_FOUND_CART_ITEM);
                });
    }

    public void decrementCartItem(Cart cart, Long itemId) {
        cart.getCartItems().stream()
                .filter(c -> c.getItemId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    int nextQuantity = cartItem.getQuantity() - 1;

                    if (nextQuantity <= 0) {
                        cartItemRepository.delete(cartItem);
                    } else {
                        cartItem.decrementQuantity();
                        cartItemRepository.save(cartItem);
                    }
                }, () -> {
                    throw new BusinessException(NOT_FOUND_CART_ITEM);
                });
    }

    public ResCartItemDto fromEntity(CartItem cartItem) {
        ItemResponse item = itemClient.getItem(cartItem.getItemId());

        return ResCartItemDto.builder()
                .itemId(cartItem.getItemId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public List<ResCartItemDto> fromEntities(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::fromEntity)
                .toList();
    }
}
