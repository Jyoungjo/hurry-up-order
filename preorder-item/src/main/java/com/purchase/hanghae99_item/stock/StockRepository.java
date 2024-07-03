package com.purchase.hanghae99_item.stock;

import com.purchase.hanghae99_item.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItemId(Long itemId);
    Optional<Stock> findByItem(Item item);
}
