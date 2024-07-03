package com.purchase.hanghae99_user.stock;

import com.purchase.hanghae99_user.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItemId(Long itemId);
    Optional<Stock> findByItem(Item item);
}
