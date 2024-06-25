package com.purchase.hanghae99.order;

import com.purchase.hanghae99.order.dto.ReqOrderDto;
import com.purchase.hanghae99.order.dto.ResOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface OrderService {
    ResOrderDto createOrder(ReqOrderDto req, Authentication authentication);
    Page<ResOrderDto> readAllOrder(Authentication authentication, Integer page, Integer size);
    ResOrderDto readOrder(Authentication authentication, Long orderId);
    ResOrderDto updateOrder(Authentication authentication, Long orderId, ReqOrderDto req);
    void deleteOrder(Authentication authentication, Long orderId);
}
