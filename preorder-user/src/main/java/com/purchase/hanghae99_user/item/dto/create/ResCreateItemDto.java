package com.purchase.hanghae99_user.item.dto.create;

import com.purchase.hanghae99_user.item.Item;
import lombok.*;

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
