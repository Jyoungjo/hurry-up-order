package com.purchase.hanghae99_user.order.dto;

import com.purchase.hanghae99_user.order_item.OrderItem;
import com.purchase.hanghae99_user.shipment.ShipmentStatus;
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
    private ShipmentStatus status;

    public static ResOrderItemDto fromEntity(OrderItem orderItem) {
        return ResOrderItemDto.builder()
                .itemId(orderItem.getItem().getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalSum(orderItem.getTotalSum())
                .status(orderItem.getShipment().getStatus())
                .build();
    }
}
