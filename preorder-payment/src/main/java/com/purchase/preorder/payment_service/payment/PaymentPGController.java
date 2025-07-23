package com.purchase.preorder.payment_service.payment;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-service/api/v1/payments")
public class PaymentPGController {
    private static final String TOSS = "TOSS";
    private static final String NICE = "NICE";

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/preferred-pg")
    public ResponseEntity<String> getPreferredPg() {
        CircuitBreaker.State state = circuitBreakerRegistry.circuitBreaker("confirmPayment").getState();
        return ResponseEntity.ok(state == CircuitBreaker.State.OPEN ? NICE : TOSS);
    }
}
