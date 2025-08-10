package com.purchase.preorder.order_service.order;

import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.OrderItem;
import com.common.domain.repository.order.OrderRepository;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.ReqOrderDto;
import com.purchase.preorder.order_service.order.dto.ResOrderDto;
import com.purchase.preorder.order_service.order_item.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderWriter {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    @Transactional
    public ResOrderDto saveOrderTransactionally(ReqOrderDto req, Long userId, List<ItemResponse> items) {
        Map<Long, ItemResponse> itemMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::getId, Function.identity()));
        int totalPrice = req.getOrderItemList().stream()
                .mapToInt(oi -> itemMap.get(oi.getItemId()).getPrice() * oi.getItemCount())
                .sum();

        Order order = orderRepository.save(Order.of(userId, totalPrice));

        List<OrderItem> orderItems = orderItemService.createOrderItems(order, req.getOrderItemList(), itemMap);
        order.addAllOrderItems(orderItems);

        return ResOrderDto.fromEntity(order);
    }
}
