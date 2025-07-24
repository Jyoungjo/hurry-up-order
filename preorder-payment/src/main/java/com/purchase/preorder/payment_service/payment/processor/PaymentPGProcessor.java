package com.purchase.preorder.payment_service.payment.processor;

import com.common.domain.entity.payment.Payment;
import com.purchase.preorder.payment_service.api.external.pg.dto.PaymentCompletionData;

public interface PaymentPGProcessor<T, C> {
    boolean supports(String pgName);
    PaymentCompletionData confirm(Payment payment, T request);
    Class<T> getRequestType();
    Class<C> getCancelRequestType();
    void cancel(Payment payment, String cancelReason);
}
