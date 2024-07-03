package com.purchase.hanghae99_user.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_user.common.security.JwtAuthFilter;
import com.purchase.hanghae99_user.order.dto.ReqOrderDto;
import com.purchase.hanghae99_user.order.dto.ReqOrderItemDto;
import com.purchase.hanghae99_user.order.dto.ResOrderDto;
import com.purchase.hanghae99_user.order.dto.ResOrderItemDto;
import com.purchase.hanghae99_user.shipment.ShipmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthFilter.class
        })
})
@AutoConfigureRestDocs
@WithMockUser(roles = "USER")
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
                        new ReqOrderItemDto(1L, 5),
                        new ReqOrderItemDto(2L, 5)
                )
        );

        ResOrderDto res = new ResOrderDto(
                1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
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
        when(orderService.createOrder(any(ReqOrderDto.class), any(Authentication.class))).thenReturn(res);

        // then
        mockMvc.perform(post("/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
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
                        new ReqOrderItemDto(1L, 5),
                        new ReqOrderItemDto(2L, 5)
                )
        );

        // when
        when(orderService.createOrder(any(ReqOrderDto.class), any(Authentication.class)))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(post("/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
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
                        1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
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
        when(orderService.readAllOrder(any(Authentication.class), anyInt(), anyInt())).thenReturn(res);

        // then
        mockMvc.perform(get("/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order/주문_목록_조회/성공"));
    }

    // READ ALL
    @DisplayName("주문 목록 조회 실패 - 존재하지 않는 유저")
    @Test
    void readAllOrderFailNotFoundUser() throws Exception {
        // given
        int page = 0;
        int size = 5;

        // when
        when(orderService.readAllOrder(any(Authentication.class), anyInt(), anyInt()))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(get("/api/v1/orders")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_목록_조회/실패/존재하지_않는_유저"));
    }

    // READ
    @DisplayName("주문 단일 조회 성공")
    @Test
    void readOrder() throws Exception {
        // given
        Long orderId = 1L;

        ResOrderDto res = new ResOrderDto(
                1L, 55000, LocalDateTime.of(2024, 6, 28, 12, 8),
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
        when(orderService.readOrder(any(Authentication.class), anyLong())).thenReturn(res);

        // then
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order/주문_단일_조회/성공"));
    }

    // READ
    @DisplayName("주문 단일 조회 실패 - 존재하지 않는 주문")
    @Test
    void readOrderFailNotFoundUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        when(orderService.readOrder(any(Authentication.class), anyLong()))
                .thenThrow(new BusinessException(NOT_FOUND_ORDER));

        // then
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_단일_조회/실패/존재하지_않는_주문"));
    }

    // READ
    @DisplayName("주문 단일 조회 실패 - 유저 불일치")
    @Test
    void readOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        when(orderService.readOrder(any(Authentication.class), anyLong()))
                .thenThrow(new BusinessException(UNAUTHORIZED_ACCESS));

        // then
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_단일_조회/실패/유저_불일치"));
    }

    // UPDATE
    @DisplayName("주문 취소 성공")
    @Test
    void cancelOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doNothing().when(orderService).cancelOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/cancel")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/주문_취소/성공"));
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
                .when(orderService).cancelOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/cancel")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_취소/실패/존재하지_않는_주문"));
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
                .when(orderService).cancelOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/cancel")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_취소/실패/유저_불일치"));
    }

    // UPDATE
    @DisplayName("반품 신청 성공")
    @Test
    void returnOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long itemId = 1L;

        // when
        doNothing().when(orderService).returnOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/return")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/반품_신청/성공"));
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
                .when(orderService).returnOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/return")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/반품_신청/실패/존재하지_않는_주문"));
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
                .when(orderService).returnOrder(any(Authentication.class), anyLong(), anyLong());

        // then
        mockMvc.perform(put("/api/v1/orders/" + orderId + "/return")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemId", String.valueOf(itemId))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/반품_신청/실패/유저_불일치"));
    }

    // UPDATE
    @DisplayName("주문 삭제 성공")
    @Test
    void deleteOrder() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doNothing().when(orderService).deleteOrder(any(Authentication.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("order/주문_삭제/성공"));
    }

    // UPDATE
    @DisplayName("주문 삭제 실패 - 존재하지 않는 주문")
    @Test
    void deleteOrderFailNotFoundOrder() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_ORDER))
                .when(orderService).deleteOrder(any(Authentication.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ORDER.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_삭제/실패/존재하지_않는_주문"));
    }

    // UPDATE
    @DisplayName("주문 삭제 실패 - 유저 불일치")
    @Test
    void deleteOrderFailNotMatchUser() throws Exception {
        // given
        Long orderId = 1L;

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS))
                .when(orderService).deleteOrder(any(Authentication.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/orders/" + orderId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("order/주문_삭제/실패/유저_불일치"));
    }
}
