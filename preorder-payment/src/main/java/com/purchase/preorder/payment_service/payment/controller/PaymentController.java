package com.purchase.preorder.payment_service.payment.controller;

import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.payment.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-service/api/v1/payments")
public class PaymentController {
    private static final String TOSS = "TOSS";
    private static final String NICE = "NICE";

    private final PaymentService paymentService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostMapping
    public ResponseEntity<Long> initiatePayment(@RequestBody ReqPaymentInitiateDto req) {
        return ResponseEntity.ok(paymentService.initiatePayment(req));
    }

    @PostMapping("/toss/confirm")
    public ResponseEntity<ResPaymentDto> confirmPayment(@RequestBody TossPaymentConfirmRequest req) {
        return ResponseEntity.ok(paymentService.confirmPayment(req));
    }

    @PostMapping("/nice/confirm")
    public void confirmPayment(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        String tid = params.get("tid");
        String orderId = params.get("orderId");
        String amount = params.get("amount");

        NicePaymentRequest req = NicePaymentRequest.of(tid, orderId, Integer.parseInt(amount));
        paymentService.confirmNicePayment(req);

        String redirectUrl = String.format(
                "http://localhost:7070/success/success.html?paymentKey=%s&orderId=%s&amount=%s",
                java.net.URLEncoder.encode(tid, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(orderId, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(amount, java.nio.charset.StandardCharsets.UTF_8)
        );

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/preferred-pg")
    public ResponseEntity<String> getPreferredPg() {
        CircuitBreaker.State state = circuitBreakerRegistry.circuitBreaker("confirmPayment").getState();
        return ResponseEntity.ok(state == CircuitBreaker.State.OPEN ? NICE : TOSS);
    }
}
