package com.purchase.preorder.order_service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqOrderItemDto {
    private Long itemId;
    private Integer itemCount;
    private Integer price;
}
