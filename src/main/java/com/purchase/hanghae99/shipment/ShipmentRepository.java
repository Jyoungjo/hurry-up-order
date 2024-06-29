package com.purchase.hanghae99.shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByStatus(ShipmentStatus status);
}
