package com.purchase.hanghae99_user.wishlist_item;

import com.purchase.hanghae99_user.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistAndItemId(Wishlist wishlist, Long itemId);
}
