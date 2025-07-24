package com.common.kafka.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class TopicNames {
    public static final String ORDER_EVENTS = "order-events";
    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String STOCK_EVENTS = "stock-events";
}
