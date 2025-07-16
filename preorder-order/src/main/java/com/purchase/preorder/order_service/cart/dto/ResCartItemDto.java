package com.purchase.preorder.order_service.cart.dto;

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
}
