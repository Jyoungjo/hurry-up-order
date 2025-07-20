package com.purchase.preorder.user_service.wishlist;

import com.purchase.preorder.user_service.wishlist.dto.ResWishListDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface WishlistService {
    void addItemToWishList(HttpServletRequest request, List<Long> itemIds) throws Exception;
    void removeItemFromWishList(HttpServletRequest request, List<Long> itemIds) throws Exception;
    ResWishListDto readMyWishList(HttpServletRequest request) throws Exception;
    void clearWishlist(HttpServletRequest request) throws Exception;
}
