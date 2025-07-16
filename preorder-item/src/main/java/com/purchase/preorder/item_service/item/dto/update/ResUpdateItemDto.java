package com.purchase.preorder.item_service.item.dto.update;

import com.purchase.preorder.item.Item;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResUpdateItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer price;

    public static ResUpdateItemDto fromEntity(Item item) {
        return ResUpdateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .build();
    }
}
