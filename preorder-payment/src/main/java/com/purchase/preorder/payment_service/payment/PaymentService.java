package com.purchase.preorder.payment_service.payment;

import com.purchase.preorder.payment_service.dto.ReqPaymentDto;
import com.purchase.preorder.payment_service.dto.ResPaymentDto;

public interface PaymentService {
    ResPaymentDto initiatePayment(ReqPaymentDto req);
    ResPaymentDto completePayment(Long paymentId);
}
