package com.purchase.hanghae99.cart_item;

import com.purchase.hanghae99.cart.Cart;
import com.purchase.hanghae99.cart.dto.ReqCartDto;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.NOT_FOUND_CART_ITEM;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
