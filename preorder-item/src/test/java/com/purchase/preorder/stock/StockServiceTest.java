package com.purchase.preorder.stock;

import com.purchase.preorder.common.RedisService;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.purchase.preorder.exception.ExceptionCode.NOT_ENOUGH_STOCK;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_STOCK;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockAsyncService stockAsyncService;

    @Mock
    private RedisService redisService;

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
    @DisplayName("재고 생성 성공")
    @Test
    void createStock() {
        // given
        int quantity = 5000;

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // when
        stockService.createStock(item, quantity);

        // then
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    // UPDATE
    @DisplayName("재고 증가 기능 성공 (params: itemId, quantity)")
    @Test
    void increaseStockWithItemId() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(redisService.increment(anyString(), anyInt())).thenReturn(6000L);
        doAnswer(invocation -> {
            Long itId = invocation.getArgument(0);
            int quant = invocation.getArgument(1);
            stock.increaseQuantity(quant);
            return null;
        }).when(stockAsyncService).asyncIncreaseStock(anyLong(), anyInt());

        // when
        stockService.increaseStock(itemId, quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(6000);
    }

    // UPDATE
    @DisplayName("재고 증가 기능 실패 - 존재하지 않는 재고 (params: itemId, quantity)")
    @Test
    void increaseStockWithItemIdFailNotFound() {
        // given
        Long itemId = 1L;
        int quantity = 1000;

        when(redisService.increment(anyString(), anyInt())).thenReturn(6000L);
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(stockAsyncService).asyncIncreaseStock(anyLong(), anyInt());

        // when

        // then
        assertThatThrownBy(() -> stockService.increaseStock(itemId, quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // UPDATE
    @DisplayName("재고 감소 기능 성공 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItem() {
        // given
        int quantity = 1000;

        when(redisService.increment(anyString(), anyInt())).thenReturn(4000L);
        doAnswer(invocation -> {
            Long itId = invocation.getArgument(0);
            int quant = invocation.getArgument(1);
            stock.decreaseQuantity(quant);
            return null;
        }).when(stockAsyncService).asyncDecreaseStock(anyLong(), anyInt());

        // when
        stockService.decreaseStock(item.getId(), quantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4000);
    }

    // UPDATE
    @DisplayName("재고 감소 기능 실패 - 존재하지 않는 재고 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItemFailNotFound() {
        // given
        int quantity = 1000;

        when(redisService.increment(anyString(), anyInt())).thenReturn(4000L);
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(stockAsyncService).asyncDecreaseStock(anyLong(), anyInt());

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(item.getId(), quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // UPDATE
    @DisplayName("재고 감소 기능 실패 - 재고의 양이 충분하지 않은 경우 (params: itemId, quantity)")
    @Test
    void decreaseStockWithItemFailNotEnough() {
        // given
        int quantity = 1000;

        when(redisService.increment(anyString(), anyInt())).thenReturn(-500L);

        // when

        // then
        assertThatThrownBy(() -> stockService.decreaseStock(item.getId(), quantity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_ENOUGH_STOCK.getMessage());
    }

    // READ
    @DisplayName("재고 조회 기능 성공")
    @Test
    void readStock() {
        // given
        Long itemId = 1L;

        when(redisService.getValues(anyString())).thenReturn("5000");

        // when
        int quantity = stockService.getStockQuantity(itemId).getQuantity();

        // then
        assertThat(stock.getQuantity()).isEqualTo(quantity);
    }

    // READ
    @DisplayName("재고 조회 기능 실패 - 존재하지 않는 재고")
    @Test
    void readStockFailNotFound() {
        // given
        Long itemId = 1L;

        when(redisService.getValues(anyString())).thenReturn(null);
        when(stockRepository.findByItemId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> stockService.getStockQuantity(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }
}
