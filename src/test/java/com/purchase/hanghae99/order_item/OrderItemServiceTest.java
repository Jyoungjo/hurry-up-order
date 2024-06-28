package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order.dto.ReqOrderItemDto;
import com.purchase.hanghae99.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {
    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem orderItem;
    private Order order;
    private Item item;

    @BeforeEach
    void init() {
        item = Item.builder()
                .id(1L)
                .name("제품명")
                .description("제품에 대한 설명입니다.")
                .price(10000)
                .build();

        order = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(new ArrayList<>())
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .item(item)
                .quantity(2)
                .unitPrice(10000)
                .status(OrderStatus.ACCEPTANCE)
                .build();
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 성공")
    @Test
    void createOrderItem() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2)
        );

        when(itemService.findItem(anyLong())).thenReturn(item);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doNothing().when(stockService).decreaseStock(any(Item.class), anyInt());

        // when
        orderItemService.createOrderItem(order, orderItemDtoList);

        // then
        verify(itemService, times(1)).findItem(anyLong());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(stockService, times(1)).decreaseStock(any(Item.class), anyInt());

        assertThat(order.getTotalSum()).isEqualTo(20000);
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 실패(존재하지 않는 물품)")
    @Test
    void createOrderItemFailNotFoundItem() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2)
        );

        when(itemService.findItem(anyLong())).thenThrow(new BusinessException(NOT_FOUND_ITEM));

        // when

        // then
        assertThatThrownBy(() -> orderItemService.createOrderItem(order, orderItemDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ITEM.getMessage());
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 실패(재고 엔티티가 없는데 시도함)")
    @Test
    void createOrderItemFailNotFoundStock() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2)
        );

        when(itemService.findItem(anyLong())).thenReturn(item);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(stockService).decreaseStock(any(Item.class), anyInt());

        // when

        // then
        assertThatThrownBy(() -> orderItemService.createOrderItem(order, orderItemDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_STOCK.getMessage());
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 실패(재고가 충분히 있지 않은 경우)")
    @Test
    void createOrderItemFailNotEnoughStock() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2)
        );

        when(itemService.findItem(anyLong())).thenReturn(item);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doThrow(new BusinessException(NOT_ENOUGH_STOCK))
                .when(stockService).decreaseStock(any(Item.class), anyInt());

        // when

        // then
        assertThatThrownBy(() -> orderItemService.createOrderItem(order, orderItemDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_ENOUGH_STOCK.getMessage());
    }

    // UPDATE
    @DisplayName("주문 - 물품 취소 기능 성공")
    @Test
    void cancelOrderItem() {
        // given
        Long itemId = 1L;
        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(List.of(orderItem))
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doNothing().when(stockService).increaseStock(any(Item.class), anyInt());

        // when
        orderItemService.cancelOrder(newOrder, itemId);

        // then
        assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(stockService, times(1)).increaseStock(any(Item.class), anyInt());
    }

    // UPDATE
    @DisplayName("주문 - 물품 취소 기능 실패(OrderItem 이 없음)")
    @Test
    void cancelOrderItemFailNotFound() {
        // given
        Long itemId = 1L;
        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> orderItemService.cancelOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER_ITEM.getMessage());
    }

    // UPDATE
    @DisplayName("주문 - 물품 취소 기능 실패(이미 배송중인 경우)")
    @Test
    void cancelOrderItemFailAlreadyShipped() {
        // given
        Long itemId = 1L;
        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .order(null)
                                .item(item)
                                .quantity(2)
                                .unitPrice(10000)
                                .status(OrderStatus.SHIPPING)
                                .build()
                ))
                .build();

        // when

        // then
        assertThatThrownBy(() -> orderItemService.cancelOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_SHIPPING.getMessage());
    }

    // UPDATE
    @DisplayName("주문 - 물품 반품 신청 기능 성공")
    @Test
    void returnOrderItem() {
        // given
        Long itemId = 1L;
        OrderItem newOrderItem = OrderItem.builder()
                .id(1L)
                .order(null)
                .item(item)
                .quantity(2)
                .unitPrice(10000)
                .status(OrderStatus.DELIVERED)
                .deliveredAt(LocalDateTime.now())
                .build();

        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(List.of(newOrderItem))
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(newOrderItem);

        // when
        orderItemService.returnOrder(newOrder, itemId);

        // then
        assertThat(newOrderItem.getStatus()).isEqualTo(OrderStatus.REQUEST_RETURN);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    // UPDATE
    @DisplayName("주문 - 물품 반품 신청 기능 실패(주문한 아이템이 존재하지 않음)")
    @Test
    void returnOrderItemFailNotFoundOrderItem() {
        // given
        Long itemId = 1L;

        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> orderItemService.returnOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER_ITEM.getMessage());
    }

    // UPDATE
    @DisplayName("주문 - 물품 반품 신청 기능 실패(배송 완료일 + 1일 초과)")
    @Test
    void returnOrderItemFailNoReturn() {
        // given
        Long itemId = 1L;

        OrderItem newOrderItem = OrderItem.builder()
                .id(1L)
                .order(null)
                .item(item)
                .quantity(2)
                .unitPrice(10000)
                .status(OrderStatus.DELIVERED)
                .deliveredAt(LocalDateTime.of(2024, 6, 27, 12, 8))
                .build();

        Order newOrder = Order.builder()
                .id(1L)
                .user(null)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalSum(null)
                .orderItemList(List.of(newOrderItem))
                .build();

        // when

        // then
        assertThatThrownBy(() -> orderItemService.returnOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NO_RETURN.getMessage());
    }
}