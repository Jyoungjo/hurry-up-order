package com.purchase.preorder.payment_service.payment;

import com.purchase.preorder.payment_service.dto.NicePaymentRequest;
import com.purchase.preorder.payment_service.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.dto.TossPaymentConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-service/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> initiatePayment(@RequestBody ReqPaymentInitiateDto req) {
        return ResponseEntity.ok(paymentService.initiatePayment(req));
    }

    @PostMapping("/toss/confirm")
    public ResponseEntity<ResPaymentDto> confirmPayment(@RequestBody TossPaymentConfirmRequest req) {
        return ResponseEntity.ok(paymentService.confirmPayment(req));
    }

    @PostMapping("/nice/confirm")
    public ResponseEntity<ResPaymentDto> confirmPayment(@RequestBody NicePaymentRequest req) {
        return ResponseEntity.ok(paymentService.confirmNicePayment(req));
    }
}
