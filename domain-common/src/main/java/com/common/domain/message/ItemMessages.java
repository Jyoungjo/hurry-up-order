package com.common.domain.message;

public final class ItemMessages {

    public static final String ITEM_NOT_FOUND = "상품을 찾을 수 없습니다.";
    public static final String ITEM_ALREADY_EXISTS = "이미 존재하는 상품입니다.";

    private ItemMessages() {
        throw new UnsupportedOperationException("Cannot instantiate constant class");
    }
}