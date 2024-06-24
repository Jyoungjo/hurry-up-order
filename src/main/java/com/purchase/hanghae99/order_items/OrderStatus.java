package com.purchase.hanghae99.order_items;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    READY("배송준비"), SHIPPING("배송중"), COMPLETE("배송완료");
    private final String status;
}
