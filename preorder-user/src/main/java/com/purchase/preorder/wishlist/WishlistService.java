package com.purchase.preorder.wishlist;

import com.purchase.preorder.wishlist.dto.ResWishListDto;
import jakarta.servlet.http.HttpServletRequest;

public interface WishlistService {
    void addItemToWishList(HttpServletRequest request, Long itemId) throws Exception;
    void removeItemFromWishList(HttpServletRequest request, Long itemId) throws Exception;
    ResWishListDto readMyWishList(HttpServletRequest request) throws Exception;
    void clearWishlist(HttpServletRequest request) throws Exception;
}
