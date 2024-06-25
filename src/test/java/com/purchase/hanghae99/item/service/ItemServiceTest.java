package com.purchase.hanghae99.item.service;

import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemRepository;
import com.purchase.hanghae99.item.ItemServiceImpl;
import com.purchase.hanghae99.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99.item.dto.update.ResUpdateItemDto;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

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
                "제품명", "제품에 대한 설명입니다.", 150000
        );

        // when
        when(itemRepository.save(any())).thenReturn(item);

        ResCreateItemDto res = itemService.createItem(req);

        // then
        assertThat(res.getName()).isEqualTo(req.getName());

        // verify
        verify(itemRepository, times(1)).save(any(Item.class));
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

        // verify
        verify(itemRepository, times(1)).findAll(any(Pageable.class));
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
}
