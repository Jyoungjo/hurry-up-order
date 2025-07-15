package com.purchase.preorder.payment_service.payment;

import com.purchase.preorder.payment_service.dto.ReqPaymentDto;
import com.purchase.preorder.payment_service.dto.ResPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-service/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResPaymentDto> initiatePayment(@RequestBody ReqPaymentDto req) {
        return ResponseEntity.ok(paymentService.initiatePayment(req));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ResPaymentDto> completePayment(@PathVariable("paymentId") Long paymentId) {
        return ResponseEntity.ok(paymentService.completePayment(paymentId));
    }
}
