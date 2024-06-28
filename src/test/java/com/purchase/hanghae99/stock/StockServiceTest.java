package com.purchase.hanghae99.stock;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.NOT_ENOUGH_STOCK;
import static com.purchase.hanghae99.common.exception.ExceptionCode.NOT_FOUND_STOCK;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Item item;
    private Stock stock;

    @BeforeEach
    void init() {
        item = Item.builder()
                .id(1L)
                .name("제품명")
                .price(15000)
                .description("이 제품에 대한 설명 입니다.")
                .deletedAt(null)
                .build();

        stock = Stock.builder()
                .item(item)
                .quantity(5000)
                .build();
    }

    // CREATE
    @DisplayName("재고 증가 기능 성공 - 기존 재고가 존재하는 경우 (params: item, quantity)")
    @Test
    void increaseStockWithItem() {
        // given
        int quantity = 1000;

        when(stockRepository.findByItem(any(Item.class))).thenReturn(Optional.of(stock));

        // when
        stockService.increaseStock(item, quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(6000);
    }

    // CREATE
    @DisplayName("재고 증가 기능 성공 - 재고 엔티티 생성하는 경우 (params: item, quantity)")
    @Test
    void increaseStock() {
        // given
        int quantity = 1000;

        Stock newStock = Stock.builder()
                .item(item)
                .quantity(0)
                .build();

        when(stockRepository.findByItem(any(Item.class))).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenReturn(newStock);

        // when
        stockService.increaseStock(item, quantity);

        // then
        assertThat(newStock.getQuantity()).isEqualTo(1000);
    }

    // CREATE
    @DisplayName("재고 증가 기능 성공 (params: itemId, quantity)")
    @Test
    void increaseStockWithItemId() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.of(stock));

        // when
        stockService.increaseStock(itemId, quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(6000);
    }

    // CREATE
    @DisplayName("재고 증가 기능 실패 - 존재하지 않는 재고 (params: itemId, quantity)")
    @Test
    void increaseStockWithItemIdFailNotFound() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> stockService.increaseStock(itemId, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // PUT
    @DisplayName("재고 감소 기능 성공 (params: item, quantity)")
    @Test
    void decreaseStockWithItem() {
        // given
        int quantity = 1000;

        when(stockRepository.findByItem(any(Item.class))).thenReturn(Optional.of(stock));

        // when
        stockService.decreaseStock(item, quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4000);
    }

    // PUT
    @DisplayName("재고 감소 기능 실패 - 존재하지 않는 재고 (params: item, quantity)")
    @Test
    void decreaseStockWithItemFailNotFound() {
        // given
        int quantity = 1000;

        when(stockRepository.findByItem(any(Item.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(item, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // PUT
    @DisplayName("재고 감소 기능 실패 - 재고의 양이 충분하지 않은 경우 (params: item, quantity)")
    @Test
    void decreaseStockWithItemFailNotEnough() {
        // given
        int quantity = 1000;

        Stock newStock = Stock.builder()
                .item(item)
                .quantity(500)
                .build();

        when(stockRepository.findByItem(any(Item.class))).thenReturn(Optional.of(newStock));

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(item, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_ENOUGH_STOCK.getMessage());
    }

    // PUT
    @DisplayName("재고 감소 기능 성공 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItemId() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.of(stock));

        // when
        stockService.decreaseStock(itemId, quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4000);
    }

    // PUT
    @DisplayName("재고 감소 기능 실패 - 존재하지 않는 재고 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItemIdFailNotFound() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(itemId, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // PUT
    @DisplayName("재고 감소 기능 실패 - 재고의 양이 충분하지 않은 경우 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItemIdFailNotEnough() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        Stock newStock = Stock.builder()
                .item(item)
                .quantity(500)
                .build();

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.of(newStock));

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(itemId, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_ENOUGH_STOCK.getMessage());
    }

    // READ
    @DisplayName("재고 조회 기능 성공")
    @Test
    void readStock() {
        // given
        Long itemId = 1L;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.of(stock));

        // when
        int quantity = stockService.getStockQuantity(itemId);

        // then
        assertThat(stock.getQuantity()).isEqualTo(quantity);
    }

    // READ
    @DisplayName("재고 조회 기능 실패 - 존재하지 않는 재고")
    @Test
    void readStockFailNotFound() {
        // given
        Long itemId = 1L;

        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> stockService.getStockQuantity(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }
}
