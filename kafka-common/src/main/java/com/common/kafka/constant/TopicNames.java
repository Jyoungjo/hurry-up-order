package com.common.kafka.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class TopicNames {
    public static final String USER_DELETED = "user-deleted";
    public static final String ORDER_DELETED = "order-deleted";
    public static final String PAYMENT_SUCCEED = "payment-succeed";
    public static final String PAYMENT_FAILURE = "payment-failure";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_CANCEL = "payment-cancel-requested-by-cancel";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_RETURN = "payment-cancel-requested-by-return";
    public static final String PAYMENT_CANCEL_REQUESTED_BY_ROLLBACK = "payment-cancel-requested-by-rollback";
    public static final String PAYMENT_CANCELED_BY_CANCEL = "payment-canceled-by-cancel";
    public static final String PAYMENT_CANCELED_BY_RETURN = "payment-canceled-by-return";
    public static final String PAYMENT_CANCELED_BY_ROLLBACK = "payment-canceled-by-rollback";
    public static final String STOCK_DECR = "stock-decrease";
    public static final String STOCK_RESERVATION_CANCELED = "stock-reservation-canceled";
    public static final String STOCK_DECREASED = "stock-decreased";
    public static final String STOCK_ROLLBACK_REQUESTED = "stock-rollback-requested";
    public static final String STOCK_REDIS_ROLLED_BACK = "stock-redis-rolled-back";
    public static final String SHIPMENT_CREATED = "shipment-created";
    public static final String SHIPMENT_CHANGED = "shipment-changed";
}
