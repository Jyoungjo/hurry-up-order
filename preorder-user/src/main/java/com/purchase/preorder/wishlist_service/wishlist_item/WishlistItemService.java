package com.purchase.preorder.wishlist_service.wishlist_item;

import com.common.domain.entity.user.Wishlist;
import com.common.domain.entity.user.WishlistItem;
import com.purchase.preorder.wishlist_service.wishlist.dto.ResWishListItemDto;

import java.util.List;

public interface WishlistItemService {
    void createWishListItem(Wishlist wishlist, List<Long> itemIds);
    void deleteWishListItem(Wishlist wishlist, List<Long> itemIds);
    List<ResWishListItemDto> fromEntities(List<WishlistItem> wishlistItems);
}
