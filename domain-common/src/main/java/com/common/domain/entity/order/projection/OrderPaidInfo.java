package com.common.domain.entity.order.projection;

import java.util.List;

public interface OrderPaidInfo {
    Long getId();
    Long getUserId();
    List<OrderItemPaidInfo> getOrderItemList();
}
