package com.purchase.preorder.payment_service.api.external.pg;

import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentResponse;
import com.purchase.preorder.payment_service_common.config.NiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "nicePaymentsClient", url = "${nicePayments.base-url}", configuration = NiceFeignConfig.class)
public interface NicePaymentsClient {
    @PostMapping(value = "/{tid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    NicePaymentResponse confirmPayment(
            @PathVariable(value = "tid") String tid, @RequestBody NicePaymentConfirmRequest nicePaymentConfirmRequest
    );

    @PostMapping(value = "/{tid}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    NicePaymentResponse cancelPayment(@PathVariable String tid, @RequestBody NicePaymentCancelRequest nicePaymentCancelRequest);
}
