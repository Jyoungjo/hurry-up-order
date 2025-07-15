package com.purchase.preorder.order;

import com.purchase.preorder.order_service.config.JpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    private Order order() {
        return Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(100)
                .orderItemList(new ArrayList<>())
                .build();
    }

    // CREATE
    @DisplayName("주문 생성 성공")
    @Test
    void createOrder() {
        // given
        Order order = order();

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getUserId()).isEqualTo(order.getUserId());
    }

    // READ ALL
    @DisplayName("주문 목록 조회 성공")
    @Test
    void readAllOrder() {
        // given
        orderRepository.save(order());

        // when
        List<Order> orderList = orderRepository.findAll();

        // then
        assertThat(orderList.size()).isEqualTo(1);
    }

    // READ
    @DisplayName("주문 단일 조회 성공")
    @Test
    void readOrder() {
        // given
        Order savedOrder = orderRepository.save(order());

        // when
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // then
        Assertions.assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getUserId()).isEqualTo(savedOrder.getUserId());
    }

    // DELETE
    @DisplayName("주문 삭제 성공")
    @Test
    void deleteOrder() {
        // given
        Order savedOrder = orderRepository.save(order());

        // when
        orderRepository.delete(savedOrder);

        // then
        assertThat(orderRepository.count()).isZero();
    }
}
