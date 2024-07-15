package com.purchase.preorder.client;

import com.purchase.preorder.client.response.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${feign.client.config.payment-service.url}")
public interface PaymentClient {
    @PostMapping("/payment-service/api/v1/payments")
    @CircuitBreaker(name = "initiatePayment", fallbackMethod = "fallback")
    @Retry(name = "initiatePayment", fallbackMethod = "fallback")
    PaymentResponse initiatePayment(@RequestBody ReqPaymentDto req);

    @PutMapping("/payment-service/api/v1/payments/{paymentId}")
    @CircuitBreaker(name = "completePayment", fallbackMethod = "fallback")
    @Retry(name = "completePayment", fallbackMethod = "fallback")
    PaymentResponse completePayment(@PathVariable("paymentId") Long paymentId);

    default String fallback(Throwable t) {
        return "Fallback response due to: " + t.getMessage();
    }
}
