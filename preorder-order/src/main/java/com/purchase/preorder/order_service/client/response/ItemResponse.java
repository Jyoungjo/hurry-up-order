package com.purchase.preorder.order_service.client.response;

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
    private Integer price;
    private LocalDateTime openTime;
    private Boolean isReserved;
    private LocalDateTime deletedAt;
}
