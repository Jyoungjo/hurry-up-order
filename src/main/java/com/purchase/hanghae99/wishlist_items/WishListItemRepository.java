package com.purchase.hanghae99.wishlist_items;

import com.purchase.hanghae99.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishListAndItemId(Wishlist wishlist, Long itemId);
}
