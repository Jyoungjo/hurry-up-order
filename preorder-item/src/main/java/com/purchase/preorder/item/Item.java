package com.purchase.preorder.item;

import com.purchase.preorder.common.BaseEntity;
import com.purchase.preorder.item.dto.create.ReqCreateItemDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_ITEM SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private LocalDateTime openTime;
    private Boolean isReserved;
    private LocalDateTime deletedAt;

    public void updateInfo(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Item of(ReqCreateItemDto req) {
        return Item.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .openTime(req.getOpenTime())
                .isReserved(req.getIsReserved())
                .build();
    }
}
