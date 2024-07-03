package com.purchase.hanghae99_item.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_item.common.security.JwtAuthFilter;
import com.purchase.hanghae99_item.stock.dto.ReqStockDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.purchase.hanghae99_core.exception.ExceptionCode.NOT_ENOUGH_STOCK;
import static com.purchase.hanghae99_core.exception.ExceptionCode.NOT_FOUND_STOCK;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StockController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthFilter.class
        })
})
@AutoConfigureRestDocs
@WithMockUser(roles = "USER")
public class StockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    // CREATE
    @DisplayName("재고 증가 성공")
    @Test
    void increaseStock() throws Exception {
        // given
        ReqStockDto req = new ReqStockDto(1L, 1000);

        // when
        doNothing().when(stockService).increaseStock(req.getItemId(), req.getQuantity());

        // then
        mockMvc.perform(post("/api/v1/stocks")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("stock/재고_증가/성공"));
    }

    // CREATE
    @DisplayName("재고 증가 실패 - 존재하지 않는 재고")
    @Test
    void increaseStockFailNotFound() throws Exception {
        // given
        ReqStockDto req = new ReqStockDto(1L, 1000);

        // when
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(stockService).increaseStock(req.getItemId(), req.getQuantity());

        // then
        mockMvc.perform(post("/api/v1/stocks")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_STOCK.getMessage()))
                .andDo(print())
                .andDo(document("stock/재고_증가/실패/존재하지_않는_재고"));
    }

    // UPDATE
    @DisplayName("재고 감소 성공")
    @Test
    void decreaseStock() throws Exception {
        // given
        ReqStockDto req = new ReqStockDto(1L, 1000);

        // when
        doNothing().when(stockService).decreaseStock(req.getItemId(), req.getQuantity());

        // then
        mockMvc.perform(put("/api/v1/stocks")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("stock/재고_감소/성공"));
    }

    // UPDATE
    @DisplayName("재고 감소 실패 - 존재하지 않는 재고")
    @Test
    void decreaseStockFailNotFound() throws Exception {
        // given
        ReqStockDto req = new ReqStockDto(1L, 1000);

        // when
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(stockService).decreaseStock(req.getItemId(), req.getQuantity());

        // then
        mockMvc.perform(put("/api/v1/stocks")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_STOCK.getMessage()))
                .andDo(print())
                .andDo(document("stock/재고_감소/실패/존재하지_않는_재고"));
    }

    // UPDATE
    @DisplayName("재고 감소 실패 - 충분하지 않은 재고")
    @Test
    void decreaseStockFailNotEnough() throws Exception {
        // given
        ReqStockDto req = new ReqStockDto(1L, 1000);

        // when
        doThrow(new BusinessException(NOT_ENOUGH_STOCK))
                .when(stockService).decreaseStock(req.getItemId(), req.getQuantity());

        // then
        mockMvc.perform(put("/api/v1/stocks")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(NOT_ENOUGH_STOCK.getMessage()))
                .andDo(print())
                .andDo(document("stock/재고_감소/실패/충분하지_않은_재고"));
    }
}
