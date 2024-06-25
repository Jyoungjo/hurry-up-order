package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import org.springframework.security.core.Authentication;

public interface WishListService {
    Long createWishList(Authentication authentication);
    void addItemToWishList(Long wishListId, Long itemId);
    void removeItemFromWishList(Long wishListId, Long itemId);
    ResWishListDto readWishList(Authentication authentication, Long wishListId);
}
