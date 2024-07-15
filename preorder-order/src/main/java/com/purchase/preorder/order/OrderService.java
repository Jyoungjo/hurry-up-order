package com.purchase.preorder.order;

import com.purchase.preorder.order.dto.ReqLimitedOrderDto;
import com.purchase.preorder.order.dto.ReqOrderDto;
import com.purchase.preorder.order.dto.ResOrderDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    ResOrderDto createOrder(ReqOrderDto req, HttpServletRequest request) throws Exception;
    ResOrderDto createOrderOfLimitedItem(ReqLimitedOrderDto req, HttpServletRequest request) throws Exception;
    Page<ResOrderDto> readAllOrder(HttpServletRequest request, Integer page, Integer size) throws Exception;
    ResOrderDto readOrder(HttpServletRequest request, Long orderId) throws Exception;
    void deleteOrder(HttpServletRequest request, Long orderId) throws Exception;
    void cancelOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception;
    void returnOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception;
}
