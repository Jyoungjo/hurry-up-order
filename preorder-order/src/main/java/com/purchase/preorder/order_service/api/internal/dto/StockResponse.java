package com.purchase.preorder.order_service.api.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long itemId;
    private Integer quantity;
}
