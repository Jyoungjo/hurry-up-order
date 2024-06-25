package com.purchase.hanghae99.order_item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);
    OrderItem findByOrderIdAndItemId(Long orderId, Long itemId);
}
