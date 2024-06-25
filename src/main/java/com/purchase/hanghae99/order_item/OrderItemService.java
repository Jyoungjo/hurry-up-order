package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.order.dto.ReqOrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {
    private final ItemService itemService;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void createOrderItem(Order order, List<ReqOrderItemDto> orderItemList) {
        orderItemList.stream().map(orderItemDto -> {
            Item foundItem = itemService.findItem(orderItemDto.getItemId());
            return OrderItem.of(order, foundItem, orderItemDto);
        }).forEach(orderItemRepository::save);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Transactional
    public void updateOrderItems(Order order, List<ReqOrderItemDto> orderItemDtoList) {
        List<OrderItem> orderItems = order.getOrderItemList();

        Map<Long, Integer> dtoMap = orderItemDtoList.stream()
                .collect(Collectors.toMap(ReqOrderItemDto::getItemId, ReqOrderItemDto::getItemCount));

        orderItems.stream()
                .filter(orderItem -> dtoMap.containsKey(orderItem.getItem().getId()))
                .forEach(orderItem -> {
                    Integer itemCount = dtoMap.get(orderItem.getItem().getId());
                    if (!itemCount.equals(orderItem.getQuantity())) {
                        orderItem.updateQuantity(itemCount);
                        if (itemCount == 0) {
                            orderItemRepository.delete(orderItem);
                        } else {
                            orderItemRepository.save(orderItem);
                        }
                    }
                });
    }
}
