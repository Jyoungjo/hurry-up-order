package com.purchase.preorder.cart_service.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCartItemDto {
    private Long itemId;
    private String name;
    private Integer price;
    private Integer quantity;

    public static ResCartItemDto of(Long itemId, String name, Integer price, Integer quantity) {
        return ResCartItemDto.builder()
                .itemId(itemId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
