package com.purchase.hanghae99_item.cart_item;

import com.purchase.hanghae99_item.cart.Cart;
import com.purchase.hanghae99_item.cart.dto.ReqCartDto;
import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_item.item.Item;
import com.purchase.hanghae99_item.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.purchase.hanghae99_core.exception.ExceptionCode.NOT_FOUND_CART_ITEM;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;

    public void createCartItem(Cart cart, ReqCartDto req) {
        Item item = itemService.findItem(req.getItemId());

        cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(req.getItemId()))
                .findFirst()
                .ifPresentOrElse(
                        cartItem -> {
                            cartItem.updateQuantity(req.getQuantity());
                            cartItemRepository.save(cartItem);
                        },
                        () -> cartItemRepository.save(CartItem.of(item, cart, req.getQuantity())));
    }

    public void incrementCartItem(Cart cart, Long itemId) {
        cart.getCartItems().stream()
                .filter(c -> c.getItem().getId().equals(itemId))
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
                .filter(c -> c.getItem().getId().equals(itemId))
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
}
