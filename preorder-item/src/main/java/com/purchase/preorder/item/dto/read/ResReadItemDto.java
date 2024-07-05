package com.purchase.preorder.item.dto.read;

import com.purchase.preorder.item.Item;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResReadItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer price;

    public static ResReadItemDto fromEntity(Item item, int quantity) {
        return ResReadItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .build();
    }
}
