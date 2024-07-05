package com.purchase.preorder.wishlist_item;

import com.purchase.preorder.common.BaseEntity;
import com.purchase.preorder.wishlist.Wishlist;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_WISHLIST_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_WISHLIST_ITEM SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class WishlistItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="wishlist_id")
    private Wishlist wishlist;
    private Long itemId;
    private LocalDateTime deletedAt;

    public static WishlistItem of(Long itemId, Wishlist wishlist) {
        return WishlistItem.builder()
                .itemId(itemId)
                .wishlist(wishlist)
                .build();
    }
}
