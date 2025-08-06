package com.common.domain.repository.order;

import com.common.domain.common.ShipmentStatus;
import com.common.domain.entity.order.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByStatus(ShipmentStatus status);
    List<Shipment> findAllByOrderItemIdIn(List<Long> orderItemIds);

    @Modifying
    @Query("UPDATE Shipment s SET s.status = :status WHERE s.orderItemId IN :ids")
    void updateStatusByOrderItemIds(@Param("status") ShipmentStatus status, @Param("ids") List<Long> orderItemIds);

    @Modifying
    @Query("UPDATE Shipment s SET s.deletedAt = :deletedAt WHERE s.orderItemId IN :ids")
    void deleteAllByOrderItemIdsIn(@Param("deletedAt") LocalDateTime deletedAt, @Param("ids") List<Long> orderItemIds);
}