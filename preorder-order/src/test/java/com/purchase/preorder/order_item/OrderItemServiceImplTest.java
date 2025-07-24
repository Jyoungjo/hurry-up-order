package com.purchase.preorder.order_item;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order.Order;
import com.purchase.preorder.order_service.api.internal.ItemClient;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.ReqOrderItemDto;
import com.purchase.preorder.order_service.order_item.service.OrderItemServiceImpl;
import com.purchase.preorder.shipment.Shipment;
import com.purchase.preorder.shipment.ShipmentStatus;
import com.purchase.preorder.shipment_service.shipment.service.ShipmentServiceImpl;
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

import static com.purchase.preorder.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceImplTest {
    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemClient itemClient;

    @Mock
    private ShipmentServiceImpl shipmentService;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;
    private Order order;
    private Shipment shipment;
    private ItemResponse item;

    @BeforeEach
    void init() {
        item = new ItemResponse(
                1L, "제품명", "제품 설명",
                10000, LocalDateTime.now(), false, null
        );

        order = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(new ArrayList<>())
                .build();

        shipment = Shipment.builder()
                .id(1L)
                .status(ShipmentStatus.ACCEPTANCE)
                .createdAt(LocalDateTime.of(2024, 6, 28, 12, 8))
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .itemId(1L)
                .quantity(2)
                .unitPrice(10000)
                .totalSum(20000)
                .shipment(shipment)
                .build();
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 성공")
    @Test
    void createOrderItem() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2, 5000)
        );

        when(itemClient.getItem(anyLong())).thenReturn(item);
        when(shipmentService.createShipment()).thenReturn(shipment);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doNothing().when(itemClient).decreaseStock(anyLong(), anyInt());

        // when
        orderItemService.createOrderItem(order, orderItemDtoList);

        // then
        verify(itemClient, times(1)).getItem(anyLong());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(itemClient, times(1)).decreaseStock(anyLong(), anyInt());
    }

    // CREATE
    @DisplayName("주문 - 물품 생성 기능 실패(존재하지 않는 물품)")
    @Test
    void createOrderItemFailNotFoundItem() {
        // given
        List<ReqOrderItemDto> orderItemDtoList = List.of(
                new ReqOrderItemDto(1L, 2, 5000)
        );

        when(itemClient.getItem(anyLong())).thenThrow(new BusinessException(NOT_FOUND_ITEM));

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
                new ReqOrderItemDto(1L, 2, 5000)
        );

        when(itemClient.getItem(anyLong())).thenReturn(item);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doThrow(new BusinessException(NOT_FOUND_STOCK))
                .when(itemClient).decreaseStock(anyLong(), anyInt());

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
                new ReqOrderItemDto(1L, 2, 5000)
        );

        when(itemClient.getItem(anyLong())).thenReturn(item);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        doThrow(new BusinessException(NOT_ENOUGH_STOCK))
                .when(itemClient).decreaseStock(anyLong(), anyInt());

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
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(List.of(orderItem))
                .build();

        doAnswer(invocation -> {
            Shipment sm = invocation.getArgument(0);
            sm.updateStatus(ShipmentStatus.CANCELLED);
            return null;
        }).when(shipmentService).cancelShipment(any(Shipment.class));
        doNothing().when(itemClient).increaseStock(anyLong(), anyInt());

        // when
        orderItemService.cancelOrder(newOrder, itemId);

        // then
        assertThat(orderItem.getShipment().getStatus()).isEqualTo(ShipmentStatus.CANCELLED);
    }

    // UPDATE
    @DisplayName("주문 - 물품 취소 기능 실패(OrderItem 이 없음)")
    @Test
    void cancelOrderItemFailNotFound() {
        // given
        Long itemId = 1L;
        Order newOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
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
        Shipment newShipment = Shipment.builder()
                .id(1L)
                .orderItem(null)
                .status(ShipmentStatus.SHIPPING)
                .createdAt(LocalDateTime.of(2024, 6, 28, 12, 8))
                .build();

        Order newOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .order(null)
                                .itemId(1L)
                                .quantity(2)
                                .unitPrice(10000)
                                .shipment(newShipment)
                                .build()
                ))
                .build();

        // when
        doThrow(new BusinessException(ALREADY_SHIPPING)).when(shipmentService).cancelShipment(any(Shipment.class));

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
        Shipment newShipment = Shipment.builder()
                .id(1L)
                .orderItem(null)
                .status(ShipmentStatus.DELIVERED)
                .createdAt(LocalDateTime.of(2024, 6, 29, 12, 8))
                .build();

        OrderItem newOrderItem = OrderItem.builder()
                .id(1L)
                .order(null)
                .itemId(1L)
                .quantity(2)
                .unitPrice(10000)
                .shipment(newShipment)
                .build();

        Order newOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(List.of(newOrderItem))
                .build();

        doAnswer(invocation -> {
            Shipment sm = invocation.getArgument(0);
            sm.updateStatus(ShipmentStatus.REQUEST_RETURN);
            return null;
        }).when(shipmentService).requestReturnShipment(any(Shipment.class));

        // when
        orderItemService.requestReturnOrder(newOrder, itemId);

        // then
        assertThat(newOrderItem.getShipment().getStatus()).isEqualTo(ShipmentStatus.REQUEST_RETURN);
    }

    // UPDATE
    @DisplayName("주문 - 물품 반품 신청 기능 실패(주문한 아이템이 존재하지 않음)")
    @Test
    void returnOrderItemFailNotFoundOrderItem() {
        // given
        Long itemId = 1L;

        Order newOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> orderItemService.requestReturnOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_ORDER_ITEM.getMessage());
    }

    // UPDATE
    @DisplayName("주문 - 물품 반품 신청 기능 실패(배송 완료일 + 1일 초과)")
    @Test
    void returnOrderItemFailNoReturn() {
        // given
        Long itemId = 1L;

        Shipment newShipment = Shipment.builder()
                .id(1L)
                .orderItem(null)
                .status(ShipmentStatus.DELIVERED)
                .createdAt(LocalDateTime.of(2024, 6, 28, 12, 8))
                .build();

        OrderItem newOrderItem = OrderItem.builder()
                .id(1L)
                .order(null)
                .itemId(1L)
                .quantity(2)
                .unitPrice(10000)
                .shipment(newShipment)
                .build();

        Order newOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .orderDate(LocalDateTime.of(2024, 6, 28, 12, 8))
                .totalPrice(null)
                .orderItemList(List.of(newOrderItem))
                .build();

        // when
        doThrow(new BusinessException(NO_RETURN)).when(shipmentService).requestReturnShipment(any(Shipment.class));

        // then
        assertThatThrownBy(() -> orderItemService.requestReturnOrder(newOrder, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NO_RETURN.getMessage());
    }
}
