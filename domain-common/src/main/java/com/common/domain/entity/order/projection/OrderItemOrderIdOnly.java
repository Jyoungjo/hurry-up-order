package com.common.domain.entity.order.projection;


import org.springframework.beans.factory.annotation.Value;

public interface OrderItemOrderIdOnly {
    Long getId();

    @Value("#{target.order.id}")
    Long getOrderId();
}
