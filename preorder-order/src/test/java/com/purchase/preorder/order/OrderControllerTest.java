package com.purchase.preorder.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order_service.order.OrderController;
import com.purchase.preorder.order_service.order.OrderService;
import com.purchase.preorder.order_service.order.dto.ReqOrderDto;
import com.purchase.preorder.order_service.order.dto.ReqOrderItemDto;
import com.purchase.preorder.order_service.order.dto.ResOrderDto;
import com.purchase.preorder.order_service.order.dto.ResOrderItemDto;
import com.purchase.preorder.shipment.ShipmentStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    // CREATE
    @DisplayName("주문 생성 확인")
    @Test
    void createOrder() throws Exception {
        // given
        ReqOrderDto req = new ReqOrderDto(
                List.of(
                        new ReqOrderItemDto(1L, 5, 5000),
                        new ReqOrderItemDto(2L, 5, 6000)
                )
        );

        ResOrderDto res = new ResOrderDto(
                1L, 1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
                List.of(
                        new ResOrderItemDto(
                                1L, 5, 5000, 25000, ShipmentStatus.ACCEPTANCE
                        ),
                        new ResOrderItemDto(
                                2L, 5, 6000, 30000, ShipmentStatus.ACCEPTANCE
                        )
                )
        );

        // when
        when(orderService.createOrder(any(ReqOrderDto.class), any(HttpServletRequest.class))).thenReturn(res);

        // then
        mockMvc.perform(post("/order-service/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("order/주문_생성/성공"));
    }

    // CREATE
    @DisplayName("주문 생성 실패 - 존재하지 않는 유저")
    @Test
    void createOrderFailNotFoundUser() throws Exception {
        // given
        ReqOrderDto req = new ReqOrderDto(
                List.of(
                        new ReqOrderItemDto(1L, 5, 5000),
                        new ReqOrderItemDto(2L, 5, 6000)
                )
        );

        // when
        when(orderService.createOrder(any(ReqOrderDto.class), any(HttpServletRequest.class)))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(post("/order-service/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_생성/실패/존재하지_않는_유저"));
    }

    // READ ALL
    @DisplayName("주문 목록 조회 성공")
    @Test
    void readAllOrder() throws Exception {
        // given
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        List<ResOrderDto> orderList = List.of(
                new ResOrderDto(
                        1L, 1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
                        List.of(
                                new ResOrderItemDto(
                                        1L, 5, 5000, 25000, ShipmentStatus.ACCEPTANCE
                                ),
                                new ResOrderItemDto(
                                        2L, 5, 6000, 30000, ShipmentStatus.ACCEPTANCE
                                )
                        )
                )
        );

        Page<ResOrderDto> res = new PageImpl<>(orderList, pageable, orderList.size());

        // when
        when(orderService.readAllOrder(any(HttpServletRequest.class), anyInt(), anyInt())).thenReturn(res);

        // then
        mockMvc.perform(get("/order-service/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order/주문_목록_조회/성공",
                        queryParameters(
                                parameterWithName("page").description("페이지 넘버"),
                                parameterWithName("size").description("표시 개수")
                        )
                ));
    }

    // READ ALL
    @DisplayName("주문 목록 조회 실패 - 존재하지 않는 유저")
    @Test
    void readAllOrderFailNotFoundUser() throws Exception {
        // given
        int page = 0;
        int size = 5;

        // when
        when(orderService.readAllOrder(any(HttpServletRequest.class), anyInt(), anyInt()))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(get("/order-service/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_목록_조회/실패/존재하지_않는_유저",
                        queryParameters(
                                parameterWithName("page").description("페이지 넘버"),
                                parameterWithName("size").description("표시 개수")
                        )
                ));
    }

    // READ
    @DisplayName("주문 단일 조회 성공")
    @Test
    void readOrder() throws Exception {
        // given
        Long orderId = 1L;

        ResOrderDto res = new ResOrderDto(
                1L, 1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
                List.of(
                        new ResOrderItemDto(
                                1L, 5, 5000, 25000, ShipmentStatus.ACCEPTANCE
                        ),
                        new ResOrderItemDto(
                                2L, 5, 6000, 30000, ShipmentStatus.ACCEPTANCE
                        )
                )
        );

        // when
        when(orderService.readOrder(any(HttpServletRequest.class), anyLong())).thenReturn(res);

        // then
        mockMvc.perform(get("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order/주문_단일_조회/성공",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }

    // READ
    @DisplayName("주문 단일 조회 실패 - 존재하지 않는 주문")
    @Test
    void readOrderFailNotFoundUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        when(orderService.readOrder(any(HttpServletRequest.class), anyLong()))
                .thenThrow(new BusinessException(NOT_FOUND_ORDER));

        // then
        mockMvc.perform(get("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_단일_조회/실패/존재하지_않는_주문",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }

    // READ
    @DisplayName("주문 단일 조회 실패 - 유저 불일치")
    @Test
    void readOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        when(orderService.readOrder(any(HttpServletRequest.class), anyLong()))
                .thenThrow(new BusinessException(UNAUTHORIZED_ACCESS));

        // then
        mockMvc.perform(get("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_단일_조회/실패/유저_불일치",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 취소 성공")
    @Test
    void cancelOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doNothing().when(orderService).cancelOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/cancel", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/주문_취소/성공",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 취소 실패 - 존재하지 않는 주문")
    @Test
    void cancelOrderFailNotFoundOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_ORDER))
                .when(orderService).cancelOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/cancel", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_취소/실패/존재하지_않는_주문",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 취소 실패 - 유저 불일치")
    @Test
    void cancelOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS))
                .when(orderService).cancelOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/cancel", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_취소/실패/유저_불일치",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("반품 신청 성공")
    @Test
    void returnOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doNothing().when(orderService).returnOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/return", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/반품_신청/성공",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("반품 신청 실패 - 존재하지 않는 주문")
    @Test
    void returnOrderFailNotFoundOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_ORDER))
                .when(orderService).returnOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/return", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/반품_신청/실패/존재하지_않는_주문",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("반품 신청 실패 - 유저 불일치")
    @Test
    void returnOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS))
                .when(orderService).returnOrder(any(HttpServletRequest.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/orders/{orderId}/return", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/반품_신청/실패/유저_불일치",
                        pathParameters(parameterWithName("orderId").description("주문 id")),
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 삭제 성공")
    @Test
    void deleteOrder() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doNothing().when(orderService).deleteOrder(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/주문_삭제/성공",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 삭제 실패 - 존재하지 않는 주문")
    @Test
    void deleteOrderFailNotFoundOrder() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_ORDER))
                .when(orderService).deleteOrder(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_삭제/실패/존재하지_않는_주문",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }

    // UPDATE
    @DisplayName("주문 삭제 실패 - 유저 불일치")
    @Test
    void deleteOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS))
                .when(orderService).deleteOrder(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/order-service/api/v1/orders/{orderId}", orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_삭제/실패/유저_불일치",
                        pathParameters(parameterWithName("orderId").description("주문 id"))
                ));
    }
}
