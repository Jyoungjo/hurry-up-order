package com.purchase.hanghae99_user.cart;

import com.purchase.hanghae99_user.cart.dto.ReqCartDto;
import com.purchase.hanghae99_user.cart.dto.ResCartDto;
import org.springframework.security.core.Authentication;

public interface CartService {
    void addItemToCart(Authentication authentication, ReqCartDto req) throws Exception;
    ResCartDto readMyCart(Authentication authentication) throws Exception;
    void incrementCartItemQuantity(Authentication authentication, Long itemId) throws Exception;
    void decrementCartItemQuantity(Authentication authentication, Long itemId) throws Exception;
    void clearCart(Authentication authentication) throws Exception;
}
