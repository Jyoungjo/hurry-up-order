package com.purchase.preorder.order_service.order_item.service;

import com.common.domain.entity.order.Order;
import com.common.domain.entity.order.OrderItem;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import com.purchase.preorder.order_service.order.dto.ReqOrderItemDto;

import java.util.List;
import java.util.Map;

public interface OrderItemService {
    List<OrderItem> createOrderItems(Order order, List<ReqOrderItemDto> orderItemList, Map<Long, ItemResponse> itemMap);
    void requestCancel(List<Long> orderItemIds);
    void requestReturn(List<Long> orderItemIds);
    void updateStatus(List<OrderItem> orderItemList, String status);
    Long updateStatusByShipment(Long shipmentId, String status);
    void assignShipments(List<OrderItem> orderItems, Map<Long, Long> shipmentMap);
    Long findOrderIdByOrderItemId(Long orderItemId);
}
