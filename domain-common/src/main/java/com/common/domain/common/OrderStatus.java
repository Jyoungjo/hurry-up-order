package com.common.domain.common;

public enum OrderStatus {
    CREATED,               // 주문 생성됨
    PAID,                  // 전체 결제 완료됨
    PARTIALLY_READY,       // 일부 배송 준비중
    READY,                 // 전체 배송 준비중
    PARTIALLY_SHIPPING,    // 일부 배송중
    SHIPPING,              // 전체 배송중
    PARTIALLY_SHIPPED,     // 일부 배송됨
    SHIPPED,               // 전체 배송됨
    CANCEL_REQUESTED,      // 취소 요청
    RETURN_REQUESTED,      // 반품 요청
    PARTIALLY_CANCELED,   // 일부 취소됨
    CANCELED,              // 전체 취소됨
    PARTIALLY_RETURNED,    // 일부 반품됨
    RETURNED,              // 전체 반품됨
    PAYMENT_FAILED,        // 결제 중 실패
    FAILED                 // 주문 처리 중 실패
}
