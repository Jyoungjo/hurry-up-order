package com.common.domain.repository.user;

import com.common.domain.entity.user.Wishlist;
import com.common.domain.entity.user.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByWishlistAndItemIdIn(Wishlist wishlist, List<Long> itemIds);
}