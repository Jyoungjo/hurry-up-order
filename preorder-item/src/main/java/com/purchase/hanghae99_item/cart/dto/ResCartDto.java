package com.purchase.hanghae99_item.cart.dto;

import com.purchase.hanghae99_item.cart.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCartDto {
    private Long cartId;
    private List<ResCartItemDto> cartItems;

    public static ResCartDto fromEntity(Cart cart) {
        return ResCartDto.builder()
                .cartId(cart.getId())
                .cartItems(cart.getCartItems().stream()
                        .map(ResCartItemDto::fromEntity)
                        .toList())
                .build();
    }
}
