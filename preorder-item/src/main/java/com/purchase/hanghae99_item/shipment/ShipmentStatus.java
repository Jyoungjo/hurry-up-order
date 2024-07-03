package com.purchase.hanghae99_item.shipment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentStatus {
    ACCEPTANCE("주문 접수"),
    READY("배송 준비"),
    SHIPPING("배송중"),
    DELIVERED("배송 완료"),
    CANCELLED("주문 취소"),
    REQUEST_RETURN("반품 신청"),
    RETURNED("반품 완료");
    private final String status;
}
