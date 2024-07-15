package com.purchase.preorder.order_item;

import com.purchase.preorder.client.ItemClient;
import com.purchase.preorder.client.response.ItemResponse;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.order.Order;
import com.purchase.preorder.order.dto.ReqLimitedOrderDto;
import com.purchase.preorder.order.dto.ReqOrderItemDto;
import com.purchase.preorder.shipment.Shipment;
import com.purchase.preorder.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_ORDER_ITEM;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final ItemClient itemClient;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentService shipmentService;

    public void createOrderItem(Order order, List<ReqOrderItemDto> orderItemList) {
        orderItemList.forEach(orderItemDto -> {
            ItemResponse foundItem = itemClient.getItem(orderItemDto.getItemId());
            // 배송 정보 생성
            Shipment shipment = shipmentService.createShipment();
            OrderItem orderItem = orderItemRepository.save(
                    OrderItem.of(order, foundItem, shipment, orderItemDto.getItemCount())
            );
            order.getOrderItemList().add(orderItem);
            // 재고 처리
            itemClient.decreaseStock(foundItem.getId(), orderItem.getQuantity());
        });
    }

    public void createOrderItem(Order order, ReqLimitedOrderDto limitedOrderDto) {
        ItemResponse foundItem = itemClient.getItem(limitedOrderDto.getItemId());

        // 배송 정보 생성
        Shipment shipment = shipmentService.createShipment();
        OrderItem orderItem = orderItemRepository.save(OrderItem.of(order, foundItem, shipment, 1));

        order.getOrderItemList().add(orderItem);
    }

    public void cancelOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER_ITEM));

        // 배송 정보 주문 취소 상태로 변경
        shipmentService.cancelShipment(orderItem.getShipment());

        // 재고 복구
        itemClient.increaseStock(orderItem.getItemId(), orderItem.getQuantity());
    }

    public void requestReturnOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER_ITEM));

        // 배송 정보 반품 신청 상태로 변경
        shipmentService.requestReturnShipment(orderItem.getShipment());
    }
}
