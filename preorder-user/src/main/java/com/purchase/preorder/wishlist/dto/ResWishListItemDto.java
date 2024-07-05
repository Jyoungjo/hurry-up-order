package com.purchase.preorder.wishlist.dto;

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
}
