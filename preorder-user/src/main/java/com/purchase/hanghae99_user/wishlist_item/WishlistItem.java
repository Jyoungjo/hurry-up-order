package com.purchase.hanghae99_user.wishlist_item;

import com.purchase.hanghae99_user.common.BaseEntity;
import com.purchase.hanghae99_user.item.Item;
import com.purchase.hanghae99_user.wishlist.Wishlist;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;
    private LocalDateTime deletedAt;

    public static WishlistItem of(Item item, Wishlist wishlist) {
        return WishlistItem.builder()
                .item(item)
                .wishlist(wishlist)
                .build();
    }
}
