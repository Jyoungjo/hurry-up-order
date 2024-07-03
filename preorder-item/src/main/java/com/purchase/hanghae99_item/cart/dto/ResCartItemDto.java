package com.purchase.hanghae99_item.cart.dto;

import com.purchase.hanghae99_item.cart_item.CartItem;
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

    public static ResCartItemDto fromEntity(CartItem cartItem) {
        return ResCartItemDto.builder()
                .itemId(cartItem.getItem().getId())
                .name(cartItem.getItem().getName())
                .price(cartItem.getItem().getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
