package com.purchase.preorder.settlement_service.settlement;

import com.purchase.preorder.payment_service.event.PaymentSucceedEvent;

public interface SettlementService {
    void create(PaymentSucceedEvent event);
    void runDailySettlementBatch();
}
