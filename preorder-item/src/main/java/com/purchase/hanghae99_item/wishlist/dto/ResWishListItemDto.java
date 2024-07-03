package com.purchase.hanghae99_item.wishlist.dto;

import com.purchase.hanghae99_item.wishlist_item.WishlistItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResWishListItemDto {
    private Long itemId;
    private String name;
    private Integer price;

    public static ResWishListItemDto fromEntity(WishlistItem wishlistItem) {
        return ResWishListItemDto.builder()
                .itemId(wishlistItem.getItem().getId())
                .name(wishlistItem.getItem().getName())
                .price(wishlistItem.getItem().getPrice())
                .build();
    }
}
