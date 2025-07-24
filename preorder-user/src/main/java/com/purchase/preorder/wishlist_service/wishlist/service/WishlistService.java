package com.purchase.preorder.wishlist_service.wishlist.service;

import com.purchase.preorder.wishlist_service.wishlist.dto.ResWishListDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface WishlistService {
    void addItemToWishList(HttpServletRequest request, List<Long> itemIds) throws Exception;
    void removeItemFromWishList(HttpServletRequest request, List<Long> itemIds) throws Exception;
    ResWishListDto readMyWishList(HttpServletRequest request) throws Exception;
    void clearWishlist(HttpServletRequest request) throws Exception;
    void delete(Long userId);
}
