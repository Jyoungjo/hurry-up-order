package com.purchase.preorder.payment_service.payment.processor;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.RedisKeyHelper;
import com.common.domain.common.PaymentPGName;
import com.common.domain.entity.payment.Payment;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.api.external.pg.NicePaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.dto.PaymentCompletionData;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentResponse;
import com.purchase.preorder.payment_service_common.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class NicePaymentPGProcessor extends AbstractPaymentPGProcessor<NicePaymentRequest, NicePaymentCancelRequest> {

    private final NicePaymentsClient client;

    public NicePaymentPGProcessor(RedisService redisService, NicePaymentsClient client) {
        super(redisService);
        this.client = client;
    }

    @Override
    public boolean supports(String pgName) {
        return PaymentPGName.TOSS.getName().equals(pgName);
    }

    @Override
    public PaymentCompletionData confirm(Payment payment, NicePaymentRequest request) {
        NicePaymentResponse res = getOrRequest(RedisKeyHelper.niceKey(request.getOrderId()), NicePaymentResponse.class,
                () -> client.confirmPayment(request.getTid(), NicePaymentConfirmRequest.of(request.getAmount())));
        if (!"DONE".equals(res.getStatus())) {
            log.warn("결제 실패 -> 결제 상태: {}", res.getStatus());
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }
        return PaymentCompletionData.mapFromNiceResponse(res);
    }

    @Override
    public Class<NicePaymentRequest> getRequestType() {
        return NicePaymentRequest.class;
    }

    @Override
    public Class<NicePaymentCancelRequest> getCancelRequestType() {
        return NicePaymentCancelRequest.class;
    }

    @Override
    @Transactional
    public void cancel(Payment payment, String cancelReason) {
        NicePaymentCancelRequest request = NicePaymentCancelRequest.with(cancelReason, payment.getPgOrderId()).build();
        NicePaymentResponse response = client.cancelPayment(payment.getPgTransactionId(), request);
        if (response.getCancels().isEmpty()) {
            log.warn("[nicepay]결제 취소 결과 찾을 수 없음");
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }
    }
}
