package com.purchase.hanghae99_order.order;

import com.purchase.hanghae99_order.order.dto.ReqOrderDto;
import com.purchase.hanghae99_order.order.dto.ResOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface OrderService {
    ResOrderDto createOrder(ReqOrderDto req, Authentication authentication) throws Exception;
    Page<ResOrderDto> readAllOrder(Authentication authentication, Integer page, Integer size) throws Exception;
    ResOrderDto readOrder(Authentication authentication, Long orderId) throws Exception;
    void deleteOrder(Authentication authentication, Long orderId) throws Exception;
    void cancelOrder(Authentication authentication, Long orderId, Long itemId) throws Exception;
    void returnOrder(Authentication authentication, Long orderId, Long itemId) throws Exception;
}
