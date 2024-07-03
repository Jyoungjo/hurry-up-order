package com.purchase.hanghae99_user.wishlist;

import com.purchase.hanghae99_user.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser(User user);
}
