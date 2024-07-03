package com.purchase.hanghae99_user.wishlist;

import com.purchase.hanghae99_user.wishlist.dto.ResWishListDto;
import org.springframework.security.core.Authentication;

public interface WishlistService {
    void addItemToWishList(Authentication authentication, Long itemId) throws Exception;
    void removeItemFromWishList(Authentication authentication, Long itemId) throws Exception;
    ResWishListDto readMyWishList(Authentication authentication) throws Exception;
    void clearWishlist(Authentication authentication) throws Exception;
}
