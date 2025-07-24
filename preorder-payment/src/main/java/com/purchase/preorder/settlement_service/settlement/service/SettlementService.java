package com.purchase.preorder.settlement_service.settlement.service;

import com.common.event_common.domain_event_vo.payment.PaymentSucceedDomainEvent;

public interface SettlementService {
    void create(PaymentSucceedDomainEvent event);
    void runDailySettlementBatch();
    void reverseSettlement(Long paymentId);
    void delete(Long paymentId);
}
