package com.common.domain.entity;

import com.common.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_WISHLIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Wishlist extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<WishlistItem> wishlistItems = new ArrayList<>();

    @Builder
    private Wishlist(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static Wishlist of(Long userId) {
        return Wishlist.builder()
                .userId(userId)
                .build();
    }

    public void addWishlistItem(WishlistItem wishlistItem) {
        this.wishlistItems.add(wishlistItem);
        wishlistItem.assignWishlist(this);
    }
}