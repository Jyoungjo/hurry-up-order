package com.purchase.hanghae99.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqStockDto {
    private Long itemId;
    private int quantity;
}
