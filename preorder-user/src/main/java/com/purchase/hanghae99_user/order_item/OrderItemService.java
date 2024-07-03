package com.purchase.hanghae99_user.order_item;

import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_user.item.Item;
import com.purchase.hanghae99_user.item.ItemService;
import com.purchase.hanghae99_user.order.Order;
import com.purchase.hanghae99_user.order.dto.ReqOrderItemDto;
import com.purchase.hanghae99_user.shipment.Shipment;
import com.purchase.hanghae99_user.shipment.ShipmentService;
import com.purchase.hanghae99_user.stock.StockService;
import com.purchase.hanghae99_core.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final ItemService itemService;
    private final OrderItemRepository orderItemRepository;
    private final StockService stockService;
    private final ShipmentService shipmentService;

    public void createOrderItem(Order order, List<ReqOrderItemDto> orderItemList) {
        int total = orderItemList.stream()
                .map(orderItemDto -> {
                    Item foundItem = itemService.findItem(orderItemDto.getItemId());
                    // 배송 정보 생성
                    Shipment shipment = shipmentService.createShipment();
                    OrderItem orderItem = orderItemRepository.save(
                            OrderItem.of(order, foundItem, shipment, orderItemDto.getItemCount())
                    );
                    // 재고 처리
                    stockService.decreaseStock(orderItem.getItem(), orderItem.getQuantity());
                    return orderItem;
                }).mapToInt(OrderItem::getTotalSum)
                .sum();

        order.saveTotalSum(total);
    }

    public void cancelOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER_ITEM));

        // 배송 정보 주문 취소 상태로 변경
        shipmentService.cancelShipment(orderItem.getShipment());

        // 재고 복구
        stockService.increaseStock(orderItem.getItem(), orderItem.getQuantity());
    }

    public void requestReturnOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER_ITEM));

        // 배송 정보 반품 신청 상태로 변경
        shipmentService.requestReturnShipment(orderItem.getShipment());
    }
}
