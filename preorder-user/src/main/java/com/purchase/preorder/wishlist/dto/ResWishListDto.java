package com.purchase.preorder.wishlist.dto;

import com.purchase.preorder.wishlist.Wishlist;
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
    private List<ResWishListItemDto> wishlistItems;

    public static ResWishListDto fromEntity(Wishlist wishlist, List<ResWishListItemDto> wishlistItems) {
        return ResWishListDto.builder()
                .wishListId(wishlist.getId())
                .wishlistItems(wishlistItems)
                .build();
    }
}
