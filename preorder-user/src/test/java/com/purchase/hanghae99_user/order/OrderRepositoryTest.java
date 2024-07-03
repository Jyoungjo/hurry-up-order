package com.purchase.hanghae99_user.order;

import com.purchase.hanghae99_user.config.JpaConfig;
import com.purchase.hanghae99_user.user.User;
import com.purchase.hanghae99_user.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static com.purchase.hanghae99_user.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Order order() {
        return Order.builder()
                .id(1L)
                .user(user)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(100)
                .orderItemList(new ArrayList<>())
                .build();
    }

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .id(1L)
                .email("email1@gmail.com")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password("asd1234!!")
                .build());
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
        assertThat(savedOrder.getUser()).isEqualTo(user);
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
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getUser()).isEqualTo(user);
    }

    // UPDATE
    @DisplayName("주문 정보 변경 성공")
    @Test
    void updateOrder() {
        // given
        Order savedOrder = orderRepository.save(order());

        int totalSum = savedOrder.getTotalSum();

        // when
        savedOrder.saveTotalSum(500);
        Order updatedOrder = orderRepository.save(savedOrder);

        // then
        assertThat(updatedOrder.getTotalSum()).isNotEqualTo(totalSum);
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
