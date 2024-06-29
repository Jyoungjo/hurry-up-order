package com.purchase.hanghae99.order;

import com.purchase.hanghae99.common.AesUtils;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.order.dto.ReqOrderDto;
import com.purchase.hanghae99.order.dto.ReqOrderItemDto;
import com.purchase.hanghae99.order.dto.ResOrderDto;
import com.purchase.hanghae99.order_item.OrderItemService;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static com.purchase.hanghae99.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .email("a3acfa0a0267531ddd493ead683a99ae")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password("asd1234!!")
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(100)
                .orderItemList(new ArrayList<>())
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");
    }

    // CREATE
    @DisplayName("주문 생성 기능 성공")
    @Test
    void createOrder() throws Exception {
        // given
        ReqOrderDto req = new ReqOrderDto(
                List.of(
                        new ReqOrderItemDto(1L, 500),
                        new ReqOrderItemDto(2L, 600)
                )
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(orderItemService).createOrderItem(any(Order.class), any());

        // when
        ResOrderDto res = orderService.createOrder(req, authentication);

        // then
        assertThat(res.getOrderId()).isEqualTo(order.getId());
    }

    // CREATE
    @DisplayName("주문 생성 기능 실패 - 존재하지 않는 유저")
    @Test
    void createOrderFailNotFoundUser() {
        // given
        ReqOrderDto req = new ReqOrderDto(
                List.of(
                        new ReqOrderItemDto(1L, 500),
                        new ReqOrderItemDto(2L, 600)
                )
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.createOrder(req, authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ ALL
    @DisplayName("주문 목록 조회 기능 성공")
    @Test
    void readAllOrder() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        int page = 0;
        int size = 5;

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        List<Order> orderList = List.of(
                new Order(
                        1L,
                        user,
                        LocalDateTime.of(2024, 6, 28, 12, 8),
                        100,
                        null,
                        new ArrayList<>()
                ),
                new Order(
                        2L,
                        user,
                        LocalDateTime.of(2024, 6, 28, 12, 15),
                        500,
                        null,
                        new ArrayList<>()
                )
        );

        when(orderRepository.findAll()).thenReturn(orderList);

        // when
        Page<ResOrderDto> res = orderService.readAllOrder(authentication, page, size);

        // then
        assertThat(res.getContent().size()).isEqualTo(2);
    }

    // READ ALL
    @DisplayName("주문 목록 조회 기능 실패 - 존재하지 않는 유저")
    @Test
    void readAllOrderFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        int page = 0;
        int size = 5;

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.readAllOrder(authentication, page, size))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("주문 단일 조회 기능 성공")
    @Test
    void readOrder() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when
        ResOrderDto res = orderService.readOrder(authentication, orderId);

        // then
        assertThat(res.getOrderId()).isEqualTo(orderId);
    }

    // READ
    @DisplayName("주문 단일 조회 기능 실패 - 존재하지 않는 주문")
    @Test
    void readOrderFailNotFoundOrder() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.readOrder(authentication, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // READ
    @DisplayName("주문 단일 조회 기능 실패 - 유저 불일치")
    @Test
    void readOrderFailNotMatchUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.readOrder(authentication, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // UPDATE
    @DisplayName("주문 취소 기능 성공")
    @Test
    void cancelOrder() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderItemService).cancelOrder(any(Order.class), anyLong());

        // when
        orderService.cancelOrder(authentication, orderId, itemId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemService, times(1)).cancelOrder(order, itemId);
    }

    // UPDATE
    @DisplayName("주문 취소 기능 실패 - 존재하지 않는 유저")
    @Test
    void cancelOrderFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.cancelOrder(authentication, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // UPDATE
    @DisplayName("주문 취소 기능 실패 - 유저 불일치")
    @Test
    void cancelOrderFailNotMatchUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.cancelOrder(authentication, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // UPDATE
    @DisplayName("반품 신청 기능 성공")
    @Test
    void returnOrder() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderItemService).requestReturnOrder(any(Order.class), anyLong());

        // when
        orderService.returnOrder(authentication, orderId, itemId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemService, times(1)).requestReturnOrder(order, itemId);
    }

    // UPDATE
    @DisplayName("반품 신청 기능 실패 - 존재하지 않는 유저")
    @Test
    void returnOrderFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.returnOrder(authentication, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // UPDATE
    @DisplayName("반품 신청 기능 실패 - 유저 불일치")
    @Test
    void returnOrderFailNotMatchUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);
        Long orderId = 1L;
        Long itemId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.returnOrder(authentication, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // DELETE
    @DisplayName("주문 삭제 기능 성공")
    @Test
    void deleteOrder() throws Exception {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any(Order.class));

        // when
        orderService.deleteOrder(authentication, orderId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    // DELETE
    @DisplayName("주문 삭제 기능 실패 - 존재하지 않는 유저")
    @Test
    void deleteOrderFailNotFoundUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.deleteOrder(authentication, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // DELETE
    @DisplayName("주문 삭제 기능 실패 - 유저 불일치")
    @Test
    void deleteOrderFailNotMatchUser() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "wrong-email@email.com", null);
        Long orderId = 1L;

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.deleteOrder(authentication, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
