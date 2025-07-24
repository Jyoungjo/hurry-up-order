package com.purchase.preorder.order_service.api.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice;
    private LocalDateTime openTime;
    private Boolean isReserved;
}
