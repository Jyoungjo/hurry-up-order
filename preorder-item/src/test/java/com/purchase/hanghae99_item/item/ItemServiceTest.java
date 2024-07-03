package com.purchase.hanghae99_item.item;

import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_item.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99_item.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99_item.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99_item.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99_item.item.dto.update.ResUpdateItemDto;
import com.purchase.hanghae99_item.stock.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockServiceImpl stockService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;

    @BeforeEach
    void init() {
        item = Item.builder()
                .id(1L)
                .name("제품명")
                .description("제품에 대한 설명입니다.")
                .price(150000)
                .build();
    }

    // CREATE
    @DisplayName("상품 등록 기능 성공")
    @Test
    void succeedAddItem() {
        // given
        ReqCreateItemDto req = new ReqCreateItemDto(
                "제품명", "제품에 대한 설명입니다.", 150000, 500
        );

        // when
        when(itemRepository.save(any())).thenReturn(item);
        doNothing().when(stockService).increaseStock(any(Item.class), anyInt());

        ResCreateItemDto res = itemService.createItem(req);

        // then
        assertThat(res.getName()).isEqualTo(req.getName());
    }

    // READ ALL
    @DisplayName("상품 목록 조회 기능 성공")
    @Test
    void succeedReadAll() {
        // given
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        List<Item> itemList =
                List.of(
                        new Item(1L, "제품1", "제품1에 대한 설명입니다.", 150000, null),
                        new Item(2L, "제품2", "제품2에 대한 설명입니다.", 170000, null)
                );

        Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());

        when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemPage);

        // when
        Page<ResReadItemDto> res = itemService.readAllItems(page, size);

        // then
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(res.getContent().get(1).getId()).isEqualTo(2L);
    }

    // READ ONE
    @DisplayName("상품 개별 조회 기능 성공")
    @Test
    void succeedReadOne() {
        // given
        Long itemId = 1L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // when
        ResReadItemDto res = itemService.readItem(itemId);

        // then
        assertThat(res.getId()).isEqualTo(itemId);

        // verify
        verify(itemRepository, times(1)).findById(1L);
    }

    // READ ONE
    @DisplayName("존재하지 않는 상품을 조회하면 실패한다.")
    @Test
    void failReadOneByNotFound() {
        // given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> itemService.readItem(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
    }

    // UPDATE
    @DisplayName("상품 정보 수정 기능 성공")
    @Test
    void succeedUpdate() {
        // given
        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "상품명", "제품에 대한 설명 수정했습니다.", 123456
        );

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // when
        ResUpdateItemDto res = itemService.updateItem(1L, req);

        // then
        assertThat(res.getName()).isEqualTo(req.getName());
        assertThat(res.getDescription()).isEqualTo(req.getDescription());
        assertThat(res.getPrice()).isEqualTo(req.getPrice());

        // verify
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    // UPDATE
    @DisplayName("존재하지 않는 상품을 수정하려하면 실패한다.")
    @Test
    void failUpdateByNotFound() {
        // given
        ReqUpdateItemDto req = new ReqUpdateItemDto(
                "상품명", "제품에 대한 설명 수정했습니다.", 123456
        );
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> itemService.updateItem(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
    }

    // DELETE
    @DisplayName("상품 삭제 기능 성공")
    @Test
    void succeedDelete() {
        // given
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(itemRepository).deleteById(anyLong());

        // when
        itemService.deleteItem(1L);

        // then

        // verify
        verify(itemRepository, times(1)).existsById(1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

    // DELETE
    @DisplayName("존재하지 않는 상품을 삭제하면 실패한다.")
    @Test
    void failDeleteByNotFound() {
        // given
        when(itemRepository.existsById(anyLong())).thenReturn(false);

        // when

        // then
        assertThatThrownBy(() -> itemService.deleteItem(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
    }

    // READ
    @DisplayName("상품 찾기 기능 확인")
    @Test
    void succeedFindItem() {
        // given
        Long itemId = 1L;

        // when
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item foundItem = itemService.findItem(itemId);

        // then
        assertThat(foundItem.getId()).isEqualTo(itemId);
    }

    // READ
    @DisplayName("상품 찾기 기능 실패 - 존재하지 않는 상품")
    @Test
    void failFindItemByNotFoundItem() {
        // given
        Long itemId = 1L;

        // when
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> itemService.findItem(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
    }
}
