package com.purchase.preorder.payment;

import com.purchase.preorder.dto.ReqPaymentDto;
import com.purchase.preorder.dto.ResPaymentDto;

public interface PaymentService {
    ResPaymentDto initiatePayment(ReqPaymentDto req);
    ResPaymentDto completePayment(Long paymentId);
}
