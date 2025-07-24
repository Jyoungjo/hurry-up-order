package com.common.domain.repository.order;

import com.common.domain.entity.order.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.itemId IN :itemIds")
    List<CartItem> findByCartIdAndItemIds(@Param("cartId") Long cartId, @Param("itemIds") List<Long> itemIds);
}
