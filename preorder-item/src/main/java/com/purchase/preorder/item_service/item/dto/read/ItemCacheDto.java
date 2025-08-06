package com.purchase.preorder.item_service.item.dto.read;

import com.common.domain.entity.item.Item;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemCacheDto {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime openTime;
    private Boolean isReserved;

    public static ItemCacheDto of(Item item, Integer quantity) {
        return ItemCacheDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .quantity(quantity)
                .price(item.getPrice())
                .totalPrice(item.getPrice() * quantity)
                .openTime(item.getOpenTime())
                .isReserved(item.getIsReserved())
                .build();
    }
}
