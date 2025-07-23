package com.purchase.preorder.payment_service.api.external;

import com.purchase.preorder.payment_service.config.NiceFeignConfig;
import com.purchase.preorder.payment_service.dto.NicePaymentConfirmRequest;
import com.purchase.preorder.payment_service.dto.NicePaymentConfirmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "nicePaymentsClient", url = "${nicePayments.base-url}", configuration = NiceFeignConfig.class)
public interface NicePaymentsClient {
    @PostMapping(value = "/{tid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    NicePaymentConfirmResponse confirmPayment(
            @PathVariable(value = "tid") String tid, @RequestBody NicePaymentConfirmRequest nicePaymentConfirmRequest
    );
}
