package com.purchase.preorder.order_service.order_item.repository;

import com.common.domain.entity.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemJDBCRepository {
    private static final int BATCH_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;

    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        String sql = """
            INSERT INTO tb_order_item
              (order_id, item_id, quantity, unit_price, total_sum,
               shipment_id, order_item_status, payment_status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        for (int i = 0; i < orderItems.size(); i += BATCH_SIZE) {
            List<OrderItem> batchList = orderItems.subList(i, Math.min(i + BATCH_SIZE, orderItems.size()));
            jdbcTemplate.batchUpdate(sql, batchList, batchList.size(), (PreparedStatement ps, OrderItem oi) -> {
                // order_id
                ps.setLong(1, oi.getOrder().getId());
                // item_id
                ps.setLong(2, oi.getItemId());
                // quantity
                ps.setInt(3, oi.getQuantity());
                // unit_price
                ps.setInt(4, oi.getUnitPrice());
                // total_sum
                ps.setInt(5, oi.getTotalSum());
                // shipment_id (nullable)
                if (oi.getShipmentId() != null) {
                    ps.setLong(6, oi.getShipmentId());
                } else {
                    ps.setNull(6, Types.BIGINT);
                }
                // order_item_status
                ps.setString(7, oi.getOrderItemStatus().name());
                // payment_status
                ps.setString(8, oi.getPaymentStatus().name());
            });
        }

        return orderItems;
    }
}
