package com.purchase.preorder.wishlist;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.wishlist.dto.ResWishListDto;
import com.purchase.preorder.wishlist.dto.ResWishListItemDto;
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

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_USER;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_WISHLIST;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {WishlistController.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class WishlistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    // CREATE
    @DisplayName("위시리스트 상품 추가 확인")
    @Test
    void addItemToWishlist() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doNothing().when(wishlistService).addItemToWishList(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(post("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("wishlist/위시리스트_상품_추가/성공",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // CREATE
    @DisplayName("위시리스트 상품 추가 실패 - 존재하지 않는 유저")
    @Test
    void addItemToWishlistFailNotFoundUser() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(wishlistService).addItemToWishList(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(post("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_상품_추가/실패/존재하지_않는_유저",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // READ
    @DisplayName("위시리스트 조회 확인")
    @Test
    void readMyWishList() throws Exception {
        // given
        ResWishListDto res = new ResWishListDto(
                1L,
                List.of(
                        new ResWishListItemDto(1L, "제품명1", 5000),
                        new ResWishListItemDto(2L, "제품명2", 4000)
                )
        );

        // when
        when(wishlistService.readMyWishList(any(HttpServletRequest.class))).thenReturn(res);

        // then
        mockMvc.perform(get("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("wishlist/위시리스트_조회/성공"));
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 유저")
    @Test
    void readMyWishListFailNotFoundUser() throws Exception {
        // given

        // when
        when(wishlistService.readMyWishList(any(HttpServletRequest.class)))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(get("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_조회/실패/존재하지_않는_유저"));
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 위시리스트")
    @Test
    void readMyWishListFailNotFoundWishlist() throws Exception {
        // given

        // when
        when(wishlistService.readMyWishList(any(HttpServletRequest.class)))
                .thenThrow(new BusinessException(NOT_FOUND_WISHLIST));

        // then
        mockMvc.perform(get("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_WISHLIST.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_조회/실패/존재하지_않는_위시리스트"));
    }

    // DELETE
    @DisplayName("위시리스트 상품 제거 확인")
    @Test
    void removeItemFromWishlist() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doNothing().when(wishlistService).removeItemFromWishList(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("wishlist/위시리스트_상품_제거/성공",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // DELETE
    @DisplayName("위시리스트 상품 제거 실패 - 존재하지 않는 유저")
    @Test
    void removeItemFromWishlistFailNotFoundUser() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(wishlistService).removeItemFromWishList(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_상품_제거/실패/존재하지_않는_유저",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // DELETE
    @DisplayName("위시리스트 상품 제거 실패 - 존재하지 않는 위시리스트")
    @Test
    void removeItemFromWishlistFailNotFoundWishlist() throws Exception {
        // given
        Long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_WISHLIST))
                .when(wishlistService).removeItemFromWishList(any(HttpServletRequest.class), anyLong());

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("itemId", String.valueOf(itemId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_WISHLIST.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_상품_제거/실패/존재하지_않는_위시리스트",
                        queryParameters(parameterWithName("itemId").description("상품 id"))
                ));
    }

    // DELETE
    @DisplayName("위시리스트 비우기 확인")
    @Test
    void clearWishlist() throws Exception {
        // given

        // when
        doNothing().when(wishlistService).clearWishlist(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists/clear")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("wishlist/위시리스트_비우기/성공"));
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 유저")
    @Test
    void clearWishlistFailNotFoundUser() throws Exception {
        // given

        // when
        doThrow(new BusinessException(NOT_FOUND_USER))
                .when(wishlistService).clearWishlist(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists/clear")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_비우기/실패/존재하지_않는_유저"));
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 위시리스트")
    @Test
    void clearWishlistFailNotFoundWishlist() throws Exception {
        // given

        // when
        doThrow(new BusinessException(NOT_FOUND_WISHLIST))
                .when(wishlistService).clearWishlist(any(HttpServletRequest.class));

        // then
        mockMvc.perform(delete("/user-service/api/v1/wishlists/clear")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_WISHLIST.getMessage()))
                .andDo(print())
                .andDo(document("wishlist/위시리스트_비우기/실패/존재하지_않는_위시리스트"));
    }
}
