package com.purchase.preorder.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.cart_service.cart.controller.CartController;
import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartItemDto;
import com.purchase.preorder.cart_service.cart.service.CartService;
import com.purchase.preorder.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_CART;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_USER;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CartController.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    // CREATE
    @DisplayName("장바구니 상품 추가 확인")
    @Test
    void addItemToCart() throws Exception {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        // when
        doNothing().when(cartService).addItemToCart(any(HttpServletRequest.class), any(ReqCartDto.class));

        // then
        mockMvc.perform(post("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("cart/장바구니_상품_추가/성공"));
    }

    // CREATE
    @DisplayName("장바구니 상품 추가 실패 - 존재하지 않는 유저")
    @Test
    void addItemToCartFailNotFoundUser() throws Exception {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(cartService).addItemToCart(any(HttpServletRequest.class), any(ReqCartDto.class));

        // then
        mockMvc.perform(post("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_상품_추가/실패/존재하지_않는_유저"));
    }

    // READ
    @DisplayName("장바구니 상품 조회 확인")
    @Test
    void readMyCart() throws Exception {
        // given
        List<ResCartItemDto> resCartItemDtoList = List.of(
                new ResCartItemDto(1L, "제품명1", 5000, 30000),
                new ResCartItemDto(2L, "제품명2", 15000, 50000)
        );

        ResCartDto res = new ResCartDto(
                1L, resCartItemDtoList
        );

        // when
        when(cartService.readMyCart(any(HttpServletRequest.class))).thenReturn(res);

        // then
        mockMvc.perform(get("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cart/장바구니_조회/성공"));
    }

    // READ
    @DisplayName("장바구니 상품 조회 실패 - 존재하지 않는 유저")
    @Test
    void readMyCartFailNotFoundUser() throws Exception {
        // given

        // when
        when(cartService.readMyCart(any(HttpServletRequest.class))).thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(get("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_조회/실패/존재하지_않는_유저"));
    }

    // READ
    @DisplayName("장바구니 상품 조회 실패 - 존재하지 않는 장바구니")
    @Test
    void readMyCartFailNotFoundCart() throws Exception {
        // given

        // when
        when(cartService.readMyCart(any(HttpServletRequest.class))).thenThrow(new BusinessException(NOT_FOUND_CART));

        // then
        mockMvc.perform(get("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_CART.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_조회/실패/존재하지_않는_장바구니"));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 증가 확인")
    @Test
    void incrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doNothing().when(cartService).incrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/increase")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_증가/성공",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 증가 실패 - 존재하지 않는 유저")
    @Test
    void incrementCartItemQuantityFailNotFoundUser() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(cartService).incrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/increase")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_증가/실패/존재하지_않는_유저",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 증가 실패 - 존재하지 않는 장바구니")
    @Test
    void incrementCartItemQuantityFailNotFoundCart() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_CART))
                .when(cartService).incrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/increase")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_CART.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_증가/실패/존재하지_않는_장바구니",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 감소 확인")
    @Test
    void decrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doNothing().when(cartService).decrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/decrease")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_감소/성공",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 감소 실패 - 존재하지 않는 유저")
    @Test
    void decrementCartItemQuantityFailNotFoundUser() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(cartService).decrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/decrease")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_감소/실패/존재하지_않는_유저",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // UPDATE
    @DisplayName("장바구니 물품 수량 감소 실패 - 존재하지 않는 장바구니")
    @Test
    void decrementCartItemQuantityFailNotFoundCart() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_CART))
                .when(cartService).decrementCartItemQuantity(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(put("/order-service/api/v1/carts/decrease")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_CART.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_물품_수량_감소/실패/존재하지_않는_장바구니",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // DELETE
    @DisplayName("장바구니 비우기 확인")
    @Test
    void clearCart() throws Exception {
        // given

        // when
        doNothing().when(cartService).clearCart(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("cart/장바구니_비우기/성공"));
    }

    // DELETE
    @DisplayName("장바구니 비우기 실패 - 존재하지 않는 유저")
    @Test
    void clearCartFailNotFoundUser() throws Exception {
        // given

        // when
        doThrow(new BusinessException(NOT_FOUND_USER)).when(cartService).clearCart(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_비우기/실패/존재하지_않는_유저"));
    }

    // DELETE
    @DisplayName("장바구니 비우기 실패 - 존재하지 않는 장바구니")
    @Test
    void clearCartFailNotFoundCart() throws Exception {
        // given

        // when
        doThrow(new BusinessException(NOT_FOUND_CART)).when(cartService).clearCart(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/order-service/api/v1/carts")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_CART.getMessage()))
                .andDo(print())
                .andDo(document("cart/장바구니_비우기/실패/존재하지_않는_장바구니"));
    }
}
