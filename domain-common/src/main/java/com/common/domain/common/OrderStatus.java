package com.common.domain.common;

public enum OrderStatus {
    CREATED,               // 주문 생성됨
    PARTIALLY_PAID,        // 일부 결제 완료됨 (복수 상품 중 일부만 결제 완료)
    PAID,                  // 전체 결제 완료됨
    PARTIALLY_SHIPPED,     // 일부 배송됨
    SHIPPED,               // 전체 배송됨
    PARTIALLY_DELIVERED,   // 일부 도착
    DELIVERED,             // 전체 도착
    PARTIALLY_CANCELLED,   // 일부 취소됨
    CANCELED,             // 전체 취소됨
    RETURNED,              // 전체 반품됨
    PARTIALLY_RETURNED,    // 일부 반품됨
    COMPLETED,             // 모든 상품 배송 완료 후 클레임 없음
    FAILED                 // 주문 처리 중 실패
}
