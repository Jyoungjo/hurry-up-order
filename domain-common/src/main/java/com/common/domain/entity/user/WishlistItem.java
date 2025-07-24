package com.common.domain.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_WISHLIST_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE TB_WISHLIST_ITEM SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private WishlistItem(Long id, Long itemId, LocalDateTime createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.createdAt = createdAt;
    }

    public static WishlistItem of(Long itemId) {
        return WishlistItem.builder()
                .itemId(itemId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void assignWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }
}