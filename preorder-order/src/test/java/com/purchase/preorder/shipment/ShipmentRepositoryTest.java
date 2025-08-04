package com.purchase.preorder.shipment;

import com.purchase.preorder.order_service_common.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static com.purchase.preorder.shipment.ShipmentStatus.READY;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class ShipmentRepositoryTest {
    @Autowired
    private ShipmentRepository shipmentRepository;

    private Shipment shipment() {
        return Shipment.builder()
                .id(1L)
                .status(ShipmentStatus.ACCEPTANCE)
                .createdAt(LocalDateTime.of(2024, 6, 29, 16, 4))
                .build();
    }

    // CREATE
    @DisplayName("배송 정보 생성 성공")
    @Test
    void 배송_정보_생성() {
        // given
        Shipment shipment = shipment();

        // when
        Shipment savedShipment = shipmentRepository.save(shipment);

        // then
        assertThat(savedShipment.getStatus()).isEqualTo(shipment.getStatus());
    }

    // UPDATE
    @DisplayName("배송 정보 수정 성공")
    @Test
    void 배송_정보_수정() {
        // given
        Shipment savedShipment = shipmentRepository.save(shipment());
        ShipmentStatus status = savedShipment.getStatus();

        // when
        savedShipment.updateStatus(READY);
        Shipment updatedShipment = shipmentRepository.save(savedShipment);

        // then
        assertThat(updatedShipment.getStatus()).isNotEqualTo(status);
        assertThat(updatedShipment.getStatus()).isEqualTo(READY);
    }
}
