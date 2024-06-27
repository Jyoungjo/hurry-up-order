package com.purchase.hanghae99.item.dto.create;

import com.purchase.hanghae99.item.Item;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResCreateItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private Integer price;

    public static ResCreateItemDto fromEntity(Item item, int stock) {
        return ResCreateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .stock(stock)
                .price(item.getPrice())
                .build();
    }
}
