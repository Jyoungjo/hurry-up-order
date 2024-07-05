package com.purchase.preorder.order.dto;

import com.purchase.preorder.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOrderDto {
    private Long orderId;
    private Integer totalSum;
    private LocalDateTime orderDate;
    private List<ResOrderItemDto> orderItemList;

    public static ResOrderDto fromEntity(Order order) {
        return ResOrderDto.builder()
                .orderId(order.getId())
                .totalSum(order.getTotalSum())
                .orderDate(order.getOrderDate())
                .orderItemList(order.getOrderItemList().stream()
                        .map(ResOrderItemDto::fromEntity)
                        .toList())
                .build();
    }
}
