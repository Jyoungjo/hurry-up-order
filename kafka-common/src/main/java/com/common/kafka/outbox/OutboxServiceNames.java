package com.common.kafka.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxServiceNames {
    USER_SERVICE("USER-SERVICE"),
    ORDER_SERVICE("ORDER-SERVICE"),
    ITEM_SERVICE("ITEM-SERVICE"),
    PAYMENT_SERVICE("PAYMENT-SERVICE");

    private final String serviceName;
}
