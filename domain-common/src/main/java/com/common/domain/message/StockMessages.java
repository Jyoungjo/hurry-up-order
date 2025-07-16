package com.common.domain.message;

public final class StockMessages {

    public static final String INSUFFICIENT_STOCK = "재고가 부족합니다.";
    public static final String STOCK_NOT_FOUND = "재고 정보가 존재하지 않습니다.";

    private StockMessages() {
        throw new UnsupportedOperationException("Cannot instantiate constant class");
    }
}