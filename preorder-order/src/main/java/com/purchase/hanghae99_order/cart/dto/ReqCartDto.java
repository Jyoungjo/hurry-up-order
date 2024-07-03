package com.purchase.hanghae99_order.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCartDto {
    private Long itemId;
    private Integer quantity;
}
