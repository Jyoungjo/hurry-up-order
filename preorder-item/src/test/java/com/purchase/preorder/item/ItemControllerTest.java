package com.purchase.preorder.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.config.JacksonConfig;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item.dto.update.ReqUpdateItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.INVALID_INPUT_VALUE;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_ITEM;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@Import(JacksonConfig.class)
@AutoConfigureRestDocs
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    // CREATE
    @DisplayName("상품 등록 확인")
    @Test
    void addItem() throws Exception {
        // given
        ReqCreateItemDto req = new ReqCreateItemDto(
                "상품명1", "상품명1에 대한 설명입니다.", 150, 50000,
                LocalDateTime.now(), false
        );

        ResCreateItemDto res = new ResCreateItemDto(
                1L, "상품명1", "상품명1에 대한 설명입니다.", 150, 50000
        );

        // when
        when(itemService.createItem(any())).thenReturn(res);

        // then
        mockMvc.perform(post("/item-service/api/v1/items")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("item/상품_등록/성공"));
    }

    // CREATE
    @DisplayName("상품 등록 실패 - 필수 입력값 누락")
    @Test
    void addItemFailMissingFields() throws Exception {
        // given
        ReqCreateItemDto req = new ReqCreateItemDto(
                "", "상품명1에 대한 설명입니다.", 150, 50000,
                LocalDateTime.now(), false
        );

        // when
        when(itemService.createItem(any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(post("/item-service/api/v1/items")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_등록/실패/필수_입력값_누락"));
    }

    // CREATE
    @DisplayName("상품 등록 실패 - 글자수 부족")
    @Test
    void addItemFailInsufficientCharacters() throws Exception {
        // given
        ReqCreateItemDto req = new ReqCreateItemDto(
                "1", "상품명1에 대한 설명입니다.", 150, 50000,
                LocalDateTime.now(), false
        );

        // when
        when(itemService.createItem(any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(post("/item-service/api/v1/items")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_등록/실패/글자수_부족"));
    }

    // CREATE
    @DisplayName("상품 등록 실패 - 글자수 초과")
    @Test
    void addItemFailExceedCharacters() throws Exception {
        // given
        ReqCreateItemDto req = new ReqCreateItemDto(
                "글자수 20자 이상이면 초과!!!!!!", "상품명1에 대한 설명입니다.", 150, 50000,
                LocalDateTime.now(), false
        );

        // when
        when(itemService.createItem(any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(post("/item-service/api/v1/items")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_등록/실패/글자수_초과"));
    }

    // READ ALL
    @DisplayName("상품 목록 조회 확인")
    @Test
    void readAllItems() throws Exception {
        // given
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        List<ResReadItemDto> itemList =
                List.of(
                        new ResReadItemDto(
                                1L, "제품1", "제품1에 대한 설명입니다.", 10000, 5000,
                                LocalDateTime.of(2024, 7, 7, 14, 0, 0),
                                true
                        ),
                        new ResReadItemDto(
                                2L, "제품2", "제품2에 대한 설명입니다.", 15000, 6000,
                                LocalDateTime.of(2024, 7, 5, 14, 0, 0),
                                false
                        )
                );

        Page<ResReadItemDto> res = new PageImpl<>(itemList, pageable, itemList.size());

        // when
        when(itemService.readAllItems(anyInt(), anyInt())).thenReturn(res);

        // then
        mockMvc.perform(get("/item-service/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("item/상품_목록_조회/성공"));
    }

    // READ ONE
    @DisplayName("상품 단일 조회 확인")
    @Test
    void readItem() throws Exception {
        // given
        long itemId = 1L;

        ResReadItemDto res = new ResReadItemDto(
                1L, "제품1", "제품1에 대한 설명입니다.", 10000, 5000,
                LocalDateTime.of(2024, 7, 7, 14, 0, 0),
                true
        );

        // when
        when(itemService.readItem(anyLong())).thenReturn(res);

        // then
        mockMvc.perform(get("/item-service/api/v1/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("item/상품_단일_조회/성공"));
    }

    // READ ONE
    @DisplayName("상품 단일 조회 실패 - 존재하지 않는 상품")
    @Test
    void readItemFailNotFound() throws Exception {
        // given
        long itemId = 1L;

        // when
        when(itemService.readItem(anyLong())).thenThrow(new BusinessException(NOT_FOUND_ITEM));

        // then
        mockMvc.perform(get("/item-service/api/v1/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ITEM.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_단일_조회/실패/존재하지_않는_상품"));
    }

    // UPDATE
    @DisplayName("상품 정보 수정 확인")
    @Test
    void updateItemInfo() throws Exception {
        // given
        long itemId = 1L;

        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "제품1", "제품1에 대한 설명입니다.", 5000
        );

        ResReadItemDto res = new ResReadItemDto(
                1L, "제품1", "제품1에 대한 설명입니다.", 10000, 5000,
                LocalDateTime.of(2024, 7, 7, 14, 0, 0),
                true
        );

        // when
        when(itemService.readItem(anyLong())).thenReturn(res);

        // then
        mockMvc.perform(put("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("item/상품_정보_수정/성공"));
    }

    // UPDATE
    @DisplayName("상품 정보 수정 실패 - 필수 입력값 누락")
    @Test
    void updateItemInfoMissingFields() throws Exception {
        // given
        long itemId = 1L;

        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "", "제품1에 대한 설명입니다.", 5000
        );

        // when
        when(itemService.updateItem(anyLong(), any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(put("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_정보_수정/실패/필수_입력값_누락"));
    }

    // UPDATE
    @DisplayName("상품 등록 실패 - 글자수 부족")
    @Test
    void updateItemInfoInsufficientCharacters() throws Exception {
        // given
        long itemId = 1L;

        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "글", "제품1에 대한 설명입니다.", 5000
        );

        // when
        when(itemService.updateItem(anyLong(), any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(put("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_정보_수정/실패/글자수_부족"));
    }

    // UPDATE
    @DisplayName("상품 등록 실패 - 글자수 초과")
    @Test
    void updateItemInfoExceedCharacters() throws Exception {
        // given
        long itemId = 1L;

        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "글자수 20자 이상이면 초과!!!!!!", "제품1에 대한 설명입니다.", 5000
        );

        // when
        when(itemService.updateItem(anyLong(), any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(put("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("item/상품_정보_수정/실패/글자수_초과"));
    }

    // DELETE
    @DisplayName("상품 삭제 확인")
    @Test
    void deleteItem() throws Exception {
        // given
        long itemId = 1L;

        // when
        doNothing().when(itemService).deleteItem(anyLong());

        // then
        mockMvc.perform(delete("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("item/상품_삭제/성공"));
    }

    // DELETE
    @DisplayName("상품 삭제 실패 - 존재하지 않는 상품")
    @Test
    void deleteItemFailNotFound() throws Exception {
        // given
        long itemId = 1L;

        // when
        doThrow(new BusinessException(NOT_FOUND_ITEM)).when(itemService).deleteItem(anyLong());

        // then
        mockMvc.perform(delete("/item-service/api/v1/items/" + itemId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("item/상품_삭제/실패/존재하지_않는_상품"));
    }
}
