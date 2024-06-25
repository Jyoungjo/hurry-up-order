package com.purchase.hanghae99.order_item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ACCEPTANCE("주문 접수"), READY("배송 준비"), SHIPPING("배송중"), COMPLETE("배송 완료");
    private final String status;
}
