package com.purchase.hanghae99.cart;

import com.purchase.hanghae99.cart.dto.ReqCartDto;
import com.purchase.hanghae99.cart.dto.ResCartDto;
import org.springframework.security.core.Authentication;

public interface CartService {
    void addItemToCart(Authentication authentication, ReqCartDto req);
    ResCartDto readMyCart(Authentication authentication);
    void incrementCartItemQuantity(Authentication authentication, Long itemId);
    void decrementCartItemQuantity(Authentication authentication, Long itemId);
    void clearCart(Authentication authentication);
}
