package com.purchase.preorder.order_service.order.dto;

import com.common.domain.entity.order.Order;
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
    private Long userId;
    private Integer totalSum;
    private LocalDateTime orderDate;
    private List<ResOrderItemDto> orderItemList;

    public static ResOrderDto fromEntity(Order order) {
        return ResOrderDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalSum(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .orderItemList(order.getOrderItemList().stream()
                        .map(ResOrderItemDto::fromEntity)
                        .toList())
                .build();
    }
}
