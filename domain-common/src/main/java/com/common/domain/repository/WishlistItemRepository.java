package com.common.domain.repository;

import com.common.domain.entity.Wishlist;
import com.common.domain.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByWishlistAndItemIdIn(Wishlist wishlist, List<Long> itemIds);
}