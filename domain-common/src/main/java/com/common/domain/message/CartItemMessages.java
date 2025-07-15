package com.common.domain.message;

public final class CartItemMessages {

    public static final String QUANTITY_CANNOT_BE_NEGATIVE = "수량은 음수일 수 없습니다.";
    public static final String QUANTITY_CANNOT_BE_ZERO_OR_LESS = "수량은 0 이하가 될 수 없습니다.";

    private CartItemMessages() {
        throw new UnsupportedOperationException("Cannot instantiate constant class");
    }
}
