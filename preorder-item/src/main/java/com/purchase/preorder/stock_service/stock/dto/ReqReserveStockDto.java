package com.purchase.preorder.stock_service.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqReserveStockDto {
    private Long itemId;
    private Integer quantity;
}
