package com.purchase.hanghae99.order.dto;

import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order_item.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
public class ResOrderDto {
    private Long orderId;
    private Integer totalSum;
    private LocalDateTime orderDate;
    private List<OrderItem> orderItemList;

    public static ResOrderDto fromEntity(Order order) {
        return ResOrderDto.builder()
                .orderId(order.getId())
                .totalSum(order.getTotalSum())
                .orderDate(order.getOrderDate())
                .orderItemList(order.getOrderItemList())
                .build();
    }
}
