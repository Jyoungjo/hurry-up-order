package com.purchase.preorder.user_service.wishlist.dto;

import com.common.domain.entity.Wishlist;
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
