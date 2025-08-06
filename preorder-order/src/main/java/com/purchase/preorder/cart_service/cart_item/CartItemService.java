package com.purchase.preorder.cart_service.cart_item;

import com.common.domain.entity.order.Cart;
import com.common.domain.entity.order.CartItem;
import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartItemDto;

import java.util.List;

public interface CartItemService {
    void createCartItem(Cart cart, List<ReqCartDto> items);
    void incrementCartItem(Cart cart, Long itemId, int amount);
    void decrementCartItem(Cart cart, Long itemId, int amount);
    List<ResCartItemDto> fromEntities(List<CartItem> cartItems);
}
