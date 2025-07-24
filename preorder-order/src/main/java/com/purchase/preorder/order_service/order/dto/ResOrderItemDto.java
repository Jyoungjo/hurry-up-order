package com.purchase.preorder.order_service.order.dto;

import com.common.domain.entity.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOrderItemDto {
    private Long itemId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalSum;

    public static ResOrderItemDto fromEntity(OrderItem orderItem) {
        return ResOrderItemDto.builder()
                .itemId(orderItem.getItemId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalSum(orderItem.getTotalSum())
                .build();
    }
}
