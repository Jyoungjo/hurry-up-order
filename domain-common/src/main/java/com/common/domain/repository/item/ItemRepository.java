package com.common.domain.repository.item;


import com.common.domain.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE Item i SET i.stockId = :stockId WHERE i.id = :itemId")
    void assignStock(@Param("stockId") Long stockId, @Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE Item i SET i.deletedAt = :deletedAt WHERE i.id IN :ids")
    void deleteByIds(@Param("deletedAt") LocalDateTime deletedAt, @Param("ids") List<Long> itemIds);
}