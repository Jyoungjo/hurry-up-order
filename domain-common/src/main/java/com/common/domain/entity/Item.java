package com.common.domain.entity;

import com.common.domain.common.BaseEntity;
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

    @Column(name = "stock_id")
    private Long stockId;

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

    public void assignStock(Long stockId) {
        this.stockId = stockId;
    }

    public static Item of(String name, String description, Integer price, LocalDateTime openTime, Boolean isReserved) {
        return Item.builder()
                .name(name)
                .description(description)
                .price(price)
                .openTime(openTime)
                .isReserved(isReserved)
                .build();
    }
}