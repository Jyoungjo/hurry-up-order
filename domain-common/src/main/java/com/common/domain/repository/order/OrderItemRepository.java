package com.common.domain.repository.order;


import com.common.domain.common.OrderItemStatus;
import com.common.domain.entity.order.OrderItem;
import com.common.domain.entity.order.projection.OrderItemOrderIdOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Query("UPDATE OrderItem oi SET oi.orderItemStatus = :status WHERE oi.shipmentId = :shipmentId")
    void updateStatusByShipmentId(@Param("status")OrderItemStatus status, @Param("shipmentId") Long shipmentId);

    Optional<OrderItemOrderIdOnly> findByShipmentId(Long shipmentId);
    Optional<OrderItemOrderIdOnly> findOrderItemOrderIdOnlyById(Long id);
}