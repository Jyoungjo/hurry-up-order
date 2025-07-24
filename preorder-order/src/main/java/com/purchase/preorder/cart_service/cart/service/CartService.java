package com.purchase.preorder.cart_service.cart.service;

import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CartService {
    void addItemToCart(HttpServletRequest request, List<ReqCartDto> req) throws Exception;
    ResCartDto readMyCart(HttpServletRequest request) throws Exception;
    void incrementCartItemQuantity(HttpServletRequest request, Long itemId, int amount) throws Exception;
    void decrementCartItemQuantity(HttpServletRequest request, Long itemId, int amount) throws Exception;
    void clearCart(HttpServletRequest request) throws Exception;
    void delete(Long userId);
}
