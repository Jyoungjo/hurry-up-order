package com.purchase.preorder.payment_service.payment.service;

import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.payment.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;

public interface PaymentService {
    Long initiatePayment(ReqPaymentInitiateDto req);
    ResPaymentDto confirmPayment(TossPaymentConfirmRequest req);
    ResPaymentDto confirmNicePayment(NicePaymentRequest req);
    void cancelPaymentByCancel(Long orderId, String cancelReason);
    void cancelPaymentByRollback(Long orderId, String cancelReason);
    void cancelPaymentByReturn(Long shipmentId, Long orderId, String cancelReason);
    void delete(Long orderId);
}
