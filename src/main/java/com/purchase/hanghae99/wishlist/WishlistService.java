package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import org.springframework.security.core.Authentication;

public interface WishlistService {
    void addItemToWishList(Authentication authentication, Long itemId);
    void removeItemFromWishList(Authentication authentication, Long itemId);
    ResWishListDto readMyWishList(Authentication authentication);
}
