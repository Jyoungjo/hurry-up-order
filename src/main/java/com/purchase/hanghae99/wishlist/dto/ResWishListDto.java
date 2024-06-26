package com.purchase.hanghae99.wishlist.dto;

import com.purchase.hanghae99.wishlist.Wishlist;
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

    public static ResWishListDto fromEntity(Wishlist wishlist) {
        return ResWishListDto.builder()
                .wishListId(wishlist.getId())
                .wishlistItems(wishlist.getWishlistItems().stream()
                        .map(ResWishListItemDto::fromEntity)
                        .toList())
                .build();
    }
}
