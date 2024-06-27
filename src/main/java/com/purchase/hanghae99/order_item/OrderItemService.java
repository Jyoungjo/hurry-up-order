package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order.dto.ReqOrderItemDto;
import com.purchase.hanghae99.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static com.purchase.hanghae99.order_item.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final ItemService itemService;
    private final OrderItemRepository orderItemRepository;
    private final StockService stockService;

    public void createOrderItem(Order order, List<ReqOrderItemDto> orderItemList) {
        int total = orderItemList.stream()
                .map(orderItemDto -> {
                    Item foundItem = itemService.findItem(orderItemDto.getItemId());
                    OrderItem orderItem = OrderItem.of(order, foundItem, orderItemDto.getItemCount());
                    orderItemRepository.save(orderItem);
                    // 재고 처리
                    stockService.decreaseStock(orderItem.getItem(), orderItem.getQuantity());
                    return orderItem;
                }).mapToInt(orderItem -> orderItem.getQuantity() * orderItem.getUnitPrice())
                .sum();

        order.saveTotalSum(total);
    }

    public void cancelOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER_ITEM));

        checkBeShipped(orderItem);

        orderItem.updateStatus(CANCELLED);
        orderItemRepository.save(orderItem);

        // 재고 복구
        stockService.increaseStock(orderItem.getItem(), orderItem.getQuantity());
    }

    public void returnOrder(Order order, Long itemId) {
        OrderItem orderItem = order.getOrderItemList().stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ORDER_ITEM));

        checkPossibleReturn(orderItem);

        orderItem.updateStatus(REQUEST_RETURN);
        orderItemRepository.save(orderItem);
    }

    private void checkBeShipped(OrderItem orderItem) {
        if (!(orderItem.getStatus().equals(ACCEPTANCE) || orderItem.getStatus().equals(READY))) {
            throw new BusinessException(ALREADY_SHIPPING);
        }
    }

    private void checkPossibleReturn(OrderItem orderItem) {
        if (!orderItem.getStatus().equals(DELIVERED) || orderItem.getDeliveredAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new BusinessException(NO_RETURN);
        }
    }
}
