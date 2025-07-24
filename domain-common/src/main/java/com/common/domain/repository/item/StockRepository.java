package com.common.domain.repository.item;

import com.common.domain.entity.item.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItemId(Long itemId);
    List<Stock> findByItemIdIn(List<Long> itemIds);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :qty WHERE s.itemId = :itemId AND s.quantity >= :qty")
    int decreaseStock(@Param("itemId") Long itemId, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity + :qty WHERE s.itemId = :itemId")
    int increaseStock(@Param("itemId") Long itemId, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Stock s SET s.deletedAt = :deletedAt WHERE s.itemId = :itemId")
    void deleteByItemIds(@Param("deletedAt") LocalDateTime deletedAt, @Param("itemId") Long itemId);
}