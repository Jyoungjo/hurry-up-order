package com.purchase.hanghae99.wishlist.dto;

import com.purchase.hanghae99.wishlist.Wishlist;
import com.purchase.hanghae99.wishlist_items.WishlistItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResWishListDto {
    private Long wishListId;
    private List<WishlistItem> wishlistItems;

    public static ResWishListDto fromEntity(Wishlist wishlist) {
        return ResWishListDto.builder()
                .wishListId(wishlist.getId())
                .wishlistItems(wishlist.getWishlistItems())
                .build();
    }
}
