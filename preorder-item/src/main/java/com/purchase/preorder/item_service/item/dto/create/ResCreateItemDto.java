package com.purchase.preorder.item_service.item.dto.create;

import com.common.domain.entity.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCreateItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private Integer price;

    public static ResCreateItemDto of(Item item, int stock) {
        return ResCreateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .stock(stock)
                .price(item.getPrice())
                .build();
    }
}
