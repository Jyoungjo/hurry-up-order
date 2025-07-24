package com.purchase.preorder.order_service.order;

import com.common.domain.common.OrderStatus;
import com.purchase.preorder.order_service.order.dto.ReqCancelOrderDto;
import com.purchase.preorder.order_service.order.dto.ReqOrderDto;
import com.purchase.preorder.order_service.order.dto.ResOrderDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {
    ResOrderDto createOrder(ReqOrderDto req, HttpServletRequest request) throws Exception;
    Page<ResOrderDto> readAllOrder(HttpServletRequest request, Integer page, Integer size) throws Exception;
    ResOrderDto readOrder(HttpServletRequest request, Long orderId) throws Exception;
    void deleteOrder(Long userId);
    void cancelOrder(HttpServletRequest request, Long orderId, ReqCancelOrderDto req) throws Exception;
    void returnOrder(HttpServletRequest request, Long orderId, List<Long> orderItemIds) throws Exception;
    void onPaymentSucceed(Long orderId);
    void onPaymentFailure(Long orderId);
    void onShipmentCancel(Long orderId, String cancelReason);
    void onShipmentReturn(Long shipmentId, Long orderItemId, String cancelReason);
    void onRedisRolledBack(Long orderId);
    void updateStatusByShipment(Long shipmentId, String status);
    void updateStatus(Long orderId, OrderStatus status);
    void updateStatusByRollback(Long orderId, OrderStatus status);
    void assignShipments(Long orderId, Map<Long, Long> shipmentMap);
}
