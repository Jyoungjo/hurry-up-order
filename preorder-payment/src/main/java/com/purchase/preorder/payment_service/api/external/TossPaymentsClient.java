package com.purchase.preorder.payment_service.api.external;

import com.purchase.preorder.payment_service.config.TossFeignConfig;
import com.purchase.preorder.payment_service.dto.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.dto.TossPaymentConfirmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tossPaymentsClient", url = "${tossPayments.payments.base-url}", configuration = TossFeignConfig.class)
public interface TossPaymentsClient {
    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    TossPaymentConfirmResponse confirmPayment(@RequestBody TossPaymentConfirmRequest tossPaymentConfirmRequest);
}
