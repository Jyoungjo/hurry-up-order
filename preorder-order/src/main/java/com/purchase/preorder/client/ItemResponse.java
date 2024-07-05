package com.purchase.preorder.client;

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
    private LocalDateTime deletedAt;
}
