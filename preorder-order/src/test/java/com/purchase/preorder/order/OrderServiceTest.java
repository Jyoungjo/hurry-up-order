package com.purchase.preorder.order;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order_service.api.internal.ItemClient;
import com.purchase.preorder.order_service.api.internal.dto.*;
import com.purchase.preorder.order_service.order.OrderServiceImpl;
import com.purchase.preorder.order_service.order.dto.ReqLimitedOrderDto;
import com.purchase.preorder.order_service.order.dto.ReqOrderDto;
import com.purchase.preorder.order_service.order.dto.ReqOrderItemDto;
import com.purchase.preorder.order_service.order.dto.ResOrderDto;
import com.purchase.preorder.order_service.order_item.service.OrderItemServiceImpl;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private UserClient userClient;
    @Mock
    private ItemClient itemClient;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemServiceImpl orderItemService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UserResponse user;
    private ItemResponse item;
    private PaymentResponse payment;
    private StockResponse stock;
    private Order order;
    private static final int TOTAL_USERS = 1000;
    private static final int SUCCESSFUL_ORDERS = 10;
    private static MockedStatic<JwtParser> jwtParser;
    private static MockedStatic<CustomCookieManager> cookieManager;

    @BeforeEach
    void init() {
        user = new UserResponse(
                1L, "이름", "test@email.com",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );

        item = new ItemResponse(
                1L, "상품명", "상품에 대한 설명입니다.",
                100, LocalDateTime.now(), false, null
        );

        payment = new PaymentResponse(1L, true);

        stock = new StockResponse(10);

        order = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(100)
                .orderItemList(new ArrayList<>())
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");

        JwtParser jwtParser = new JwtParser();
        jwtParser.setKey("abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @BeforeAll
    static void beforeAll() {
        jwtParser = mockStatic(JwtParser.class);
        cookieManager = mockStatic(CustomCookieManager.class);
    }

    @AfterAll
    static void afterAll() {
        jwtParser.close();
        cookieManager.close();
    }

    // CREATE
    @DisplayName("주문 생성 기능 성공")
    @Test
    void createOrder() throws Exception {
        // given
        ReqOrderDto req = new ReqOrderDto(
                List.of(
                        new ReqOrderItemDto(1L, 500, 5000),
                        new ReqOrderItemDto(2L, 600, 5000)
                )
        );

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(orderItemService).createOrderItem(any(Order.class), any(List.class));

        // when
        ResOrderDto res = orderService.createOrder(req, request);

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
                        new ReqOrderItemDto(1L, 500, 5000),
                        new ReqOrderItemDto(2L, 600, 5000)
                )
        );

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> orderService.createOrder(req, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // CREATE
    @DisplayName("선착순 주문 생성 기능 성공")
    @Test
    void createOrderOfLimitedItem() throws Exception {
        // given
        ReqLimitedOrderDto req = new ReqLimitedOrderDto(1L, 10000);
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        when(itemClient.getStock(anyLong())).thenReturn(stock);
        when(itemClient.getItem(anyLong())).thenReturn(item);
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(itemClient).decreaseStock(anyLong(), anyInt());
        when(paymentClient.initiatePayment(any(ReqPaymentDto.class))).thenReturn(payment);
        when(paymentClient.completePayment(anyLong())).thenReturn(payment);
        doNothing().when(orderItemService).createOrderItem(any(ItemResponse.class), any(Order.class));

        // when
        ResOrderDto res = orderService.createOrderOfLimitedItem(req, request);

        // then
        assertThat(res.getOrderId()).isEqualTo(order.getId());
    }

    // READ ALL
    @DisplayName("주문 목록 조회 기능 성공")
    @Test
    void readAllOrder() throws Exception {
        // given
        int page = 0;
        int size = 5;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> orderList = new PageImpl<>(
                List.of(
                        new Order(
                                1L,
                                1L,
                                LocalDateTime.of(2024, 6, 28, 12, 8),
                                100,
                                null,
                                new ArrayList<>()
                        ),
                        new Order(
                                2L,
                                1L,
                                LocalDateTime.of(2024, 6, 28, 12, 15),
                                500,
                                null,
                                new ArrayList<>()
                        )
                ), pageable, 2
        );

        when(orderRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(orderList);

        // when
        Page<ResOrderDto> res = orderService.readAllOrder(request, page, size);

        // then
        assertThat(res.getContent().size()).isEqualTo(2);
    }

    // READ ALL
    @DisplayName("주문 목록 조회 기능 실패 - 존재하지 않는 유저")
    @Test
    void readAllOrderFailNotFoundUser() {
        // given
        int page = 0;
        int size = 5;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> orderService.readAllOrder(request, page, size))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("주문 단일 조회 기능 성공")
    @Test
    void readOrder() throws Exception {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when
        ResOrderDto res = orderService.readOrder(request, orderId);

        // then
        assertThat(res.getOrderId()).isEqualTo(orderId);
    }

    // READ
    @DisplayName("주문 단일 조회 기능 실패 - 존재하지 않는 주문")
    @Test
    void readOrderFailNotFoundOrder() {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.readOrder(request, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // READ
    @DisplayName("주문 단일 조회 기능 실패 - 유저 불일치")
    @Test
    void readOrderFailNotMatchUser() {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        UserResponse newUser = new UserResponse(
                2L, "이름", "test@email.com",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );
        when(userClient.getUserByEmail(anyString())).thenReturn(newUser);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.readOrder(request, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // UPDATE
    @DisplayName("주문 취소 기능 성공")
    @Test
    void cancelOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderItemService).cancelOrder(any(Order.class), anyLong());

        // when
        orderService.cancelOrder(request, orderId, itemId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemService, times(1)).cancelOrder(order, itemId);
    }

    // UPDATE
    @DisplayName("주문 취소 기능 실패 - 존재하지 않는 주문")
    @Test
    void cancelOrderFailNotFoundUser() {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.cancelOrder(request, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // UPDATE
    @DisplayName("주문 취소 기능 실패 - 유저 불일치")
    @Test
    void cancelOrderFailNotMatchUser() {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        UserResponse newUser = new UserResponse(
                2L, "이름", "test@email.com",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );
        when(userClient.getUserByEmail(anyString())).thenReturn(newUser);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.cancelOrder(request, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // UPDATE
    @DisplayName("반품 신청 기능 성공")
    @Test
    void returnOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderItemService).requestReturnOrder(any(Order.class), anyLong());

        // when
        orderService.returnOrder(request, orderId, itemId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemService, times(1)).requestReturnOrder(order, itemId);
    }

    // UPDATE
    @DisplayName("반품 신청 기능 실패 - 존재하지 않는 주문")
    @Test
    void returnOrderFailNotFoundUser() {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.returnOrder(request, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // UPDATE
    @DisplayName("반품 신청 기능 실패 - 유저 불일치")
    @Test
    void returnOrderFailNotMatchUser() {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        UserResponse newUser = new UserResponse(
                2L, "이름", "test@email.com",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );
        when(userClient.getUserByEmail(anyString())).thenReturn(newUser);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.returnOrder(request, orderId, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // DELETE
    @DisplayName("주문 삭제 기능 성공")
    @Test
    void deleteOrder() throws Exception {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any(Order.class));

        // when
        orderService.deleteOrder(request, orderId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    // DELETE
    @DisplayName("주문 삭제 기능 실패 - 존재하지 않는 주문")
    @Test
    void deleteOrderFailNotFoundUser() {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> orderService.deleteOrder(request, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER.getMessage());
    }

    // DELETE
    @DisplayName("주문 삭제 기능 실패 - 유저 불일치")
    @Test
    void deleteOrderFailNotMatchUser() {
        // given
        Long orderId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        UserResponse newUser = new UserResponse(
                2L, "이름", "test@email.com",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );
        when(userClient.getUserByEmail(anyString())).thenReturn(newUser);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // when

        // then
        assertThatThrownBy(() -> orderService.deleteOrder(request, orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
