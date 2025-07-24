package com.common.kafka.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class KafkaEventType {
    public static final String USER_DELETED = "USER_DELETED";
    public static final String ORDER_DELETED = "ORDER_DELETED";
    public static final String STOCK_DECREASE_REQ = "STOCK_DECREASE_REQ";
    public static final String STOCK_DECREASED = "STOCK_DECREASED";
    public static final String STOCK_RESERVATION_CANCELED = "STOCK_RESERVATION_CANCELED";
    public static final String STOCK_ROLLBACK_REQUESTED = "STOCK_ROLLBACK_REQUESTED";
    public static final String STOCK_REDIS_ROLLED_BACK = "STOCK_REDIS_ROLLED_BACK";
    public static final String PAYMENT_SUCCEED = "PAYMENT_SUCCEED";
    public static final String PAYMENT_FAILURE = "PAYMENT_FAILURE";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_CANCEL = "PAYMENT_CANCEL_REQUESTED_BY_CANCEL";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_RETURN = "PAYMENT_CANCEL_REQUESTED_BY_RETURN";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK = "PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK";
    public static final String PAYMENT_CANCELED_BY_CANCEL = "PAYMENT_CANCELED_BY_CANCEL";
    public static final String PAYMENT_CANCELED_BY_RETURN = "PAYMENT_CANCELED_BY_RETURN";
    public static final String PAYMENT_CANCELED_BY_ROLLBACK = "PAYMENT_CANCELED_BY_ROLLBACK";
    public static final String SHIPMENT_CREATED = "SHIPMENT_CREATED";
    public static final String SHIPMENT_CHANGED = "SHIPMENT_CHANGED";
}
