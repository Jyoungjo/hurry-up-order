package com.common.domain.repository.order;

import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.projection.OrderPaidInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findAllByUserId(Long userId);
    Optional<OrderPaidInfo> findOrderPaidInfoByOrderId(Long orderId);
}