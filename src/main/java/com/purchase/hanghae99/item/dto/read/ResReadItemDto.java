package com.purchase.hanghae99.item.dto.read;

import com.purchase.hanghae99.item.Item;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResReadItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private Integer price;

    public static ResReadItemDto fromEntity(Item item, int quantity) {
        return ResReadItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .stock(quantity)
                .price(item.getPrice())
                .build();
    }
}
