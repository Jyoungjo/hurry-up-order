package com.purchase.preorder.item.dto.read;

import com.purchase.preorder.item.Item;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResReadItemDto {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer price;
    private LocalDateTime openTime;
    private Boolean isReserved;


    public static ResReadItemDto fromEntity(Item item, int quantity) {
        return ResReadItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .quantity(quantity)
                .price(item.getPrice())
                .openTime(item.getOpenTime())
                .isReserved(item.getIsReserved())
                .build();
    }
}
