package com.purchase.preorder.user_service.wishlist.dto;

import com.purchase.preorder.user_service.client.ItemResponse;
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

    public static ResWishListItemDto from(ItemResponse item) {
        return ResWishListItemDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build();
    }
}
