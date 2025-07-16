package com.purchase.preorder.order_service.cart.dto;

import com.purchase.preorder.cart.Cart;
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

    public static ResCartDto fromEntity(Cart cart, List<ResCartItemDto> cartItemDtoList) {
        return ResCartDto.builder()
                .cartId(cart.getId())
                .cartItems(cartItemDtoList)
                .build();
    }
}
