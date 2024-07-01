package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.config.JpaConfig;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemRepository;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order.OrderRepository;
import com.purchase.hanghae99.shipment.Shipment;
import com.purchase.hanghae99.shipment.ShipmentRepository;
import com.purchase.hanghae99.shipment.ShipmentStatus;
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

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private Order order;
    private Item item;
    private Shipment shipment;

    private OrderItem orderItem() {
        return OrderItem.builder()
                .id(1L)
                .order(order)
                .item(item)
                .quantity(50)
                .unitPrice(10000)
                .shipment(shipment)
                .build();
    }

    @BeforeEach
    void init() {
        order = orderRepository.save(Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(100)
                .orderItemList(new ArrayList<>())
                .build());

        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("제품명")
                .price(150000)
                .description("이 제품에 대한 설명 입니다.")
                .deletedAt(null)
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
        assertThat(savedOrderItem.getItem()).isEqualTo(orderItem.getItem());
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
        assertThat(foundOrderItem.get().getItem()).isEqualTo(savedOrderItem.getItem());
    }
}
