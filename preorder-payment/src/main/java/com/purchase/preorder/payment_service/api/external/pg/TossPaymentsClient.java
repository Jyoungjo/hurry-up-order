package com.purchase.preorder.payment_service.api.external.pg;

import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentResponse;
import com.purchase.preorder.payment_service_common.config.TossFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tossPaymentsClient", url = "${tossPayments.payments.base-url}", configuration = TossFeignConfig.class)
public interface TossPaymentsClient {
    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    TossPaymentResponse confirmPayment(@RequestBody TossPaymentConfirmRequest tossPaymentConfirmRequest);

    @PostMapping(value = "/{paymentKey}/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    TossPaymentResponse cancelPayment(@PathVariable String paymentKey, @RequestBody TossPaymentCancelRequest tossPaymentCancelRequest);
}
