package com.purchase.preorder.order_service.cart;

import com.purchase.preorder.order_service.cart.dto.ReqCartDto;
import com.purchase.preorder.order_service.cart.dto.ResCartDto;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    void addItemToCart(HttpServletRequest request, ReqCartDto req) throws Exception;
    ResCartDto readMyCart(HttpServletRequest request) throws Exception;
    void incrementCartItemQuantity(HttpServletRequest request, Long itemId) throws Exception;
    void decrementCartItemQuantity(HttpServletRequest request, Long itemId) throws Exception;
    void clearCart(HttpServletRequest request) throws Exception;
}
