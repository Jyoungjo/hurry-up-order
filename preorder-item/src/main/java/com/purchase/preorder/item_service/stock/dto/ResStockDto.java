package com.purchase.preorder.item_service.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResStockDto {
    private Integer quantity;

    public static ResStockDto of(int quantity) {
        return ResStockDto.builder()
                .quantity(quantity)
                .build();
    }
}
