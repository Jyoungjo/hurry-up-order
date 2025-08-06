package com.purchase.preorder.user_service.api.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private LocalDateTime openTime;
    private Boolean isReserved;
    private LocalDateTime deletedAt;
}
