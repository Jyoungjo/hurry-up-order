package com.purchase.preorder.order_item;

import com.purchase.preorder.config.JpaConfig;
import com.purchase.preorder.order.Order;
import com.purchase.preorder.order.OrderRepository;
import com.purchase.preorder.shipment.Shipment;
import com.purchase.preorder.shipment.ShipmentRepository;
import com.purchase.preorder.shipment.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private Order order;
    private Shipment shipment;

    private OrderItem orderItem() {
        return OrderItem.builder()
                .id(1L)
                .order(order)
                .itemId(1L)
                .quantity(50)
                .unitPrice(10000)
                .shipment(shipment)
                .build();
    }

    @BeforeEach
    void init() {
        order = orderRepository.save(Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(100)
                .orderItemList(new ArrayList<>())
                .build());

        shipment = shipmentRepository.save(Shipment.builder()
                .id(1L)
                .orderItem(null)
                .status(ShipmentStatus.ACCEPTANCE)
                .createdAt(LocalDateTime.of(2024, 6, 28, 12, 8))
                .build());
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 성공")
    @Test
    void createOrderItem() {
        // given
        OrderItem orderItem = orderItem();

        // when
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // then
        assertThat(savedOrderItem.getItemId()).isEqualTo(orderItem.getItemId());
    }

    // READ
    @DisplayName("주문 - 물품 조회 성공")
    @Test
    void readOrderItem() {
        // given
        OrderItem savedOrderItem = orderItemRepository.save(orderItem());

        // when
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(savedOrderItem.getId());

        // then
        assertThat(foundOrderItem).isPresent();
        assertThat(foundOrderItem.get().getItemId()).isEqualTo(savedOrderItem.getItemId());
    }
}
