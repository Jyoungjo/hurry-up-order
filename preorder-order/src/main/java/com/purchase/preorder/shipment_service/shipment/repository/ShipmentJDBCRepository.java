package com.purchase.preorder.shipment_service.shipment.repository;

import com.common.domain.entity.order.Shipment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShipmentJDBCRepository {
    private static final int BATCH_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> saveAll(List<Shipment> shipments) {
        List<Long> shipmentIds = new ArrayList<>();
        for (int i = 0; i < shipments.size(); i += BATCH_SIZE) {
            List<Shipment> batchList = shipments.subList(i, Math.min(i + BATCH_SIZE, shipments.size()));

            StringBuilder sql = new StringBuilder(
                    "INSERT INTO tb_shipment(order_item_id, status) VALUES "
            );

            List<Object> params = new ArrayList<>(batchList.size() * 2);
            for (int j = 0; j < batchList.size(); j++) {
                sql.append("(?, ?)");
                if (j < batchList.size() - 1) sql.append(", ");
                Shipment s = batchList.get(j);
                params.add(s.getOrderItemId());
                params.add(s.getStatus().name());
            }
            sql.append(" RETURNING id");

            shipmentIds.addAll(jdbcTemplate.query(
                    sql.toString(),
                    (rs, rowNum) -> rs.getLong("id"),
                    params.toArray()
            ));
        }

        return shipmentIds;
    }
}
