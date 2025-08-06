package com.purchase.preorder.order_service.order.dto;

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

    public static ReqReserveStockDto of(Long itemId, Integer quantity) {
        return ReqReserveStockDto.builder()
                .itemId(itemId)
                .quantity(quantity)
                .build();
    }
}
