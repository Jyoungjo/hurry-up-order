package com.purchase.preorder.wishlist_item;

import com.purchase.preorder.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistAndItemId(Wishlist wishlist, Long itemId);
}
