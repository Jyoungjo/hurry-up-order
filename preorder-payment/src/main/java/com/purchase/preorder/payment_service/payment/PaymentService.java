package com.purchase.preorder.payment_service.payment;

import com.purchase.preorder.payment_service.dto.NicePaymentRequest;
import com.purchase.preorder.payment_service.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.dto.TossPaymentConfirmRequest;

public interface PaymentService {
    Long initiatePayment(ReqPaymentInitiateDto req);
    ResPaymentDto confirmPayment(TossPaymentConfirmRequest req);
    ResPaymentDto confirmNicePayment(NicePaymentRequest req);
}
