package com.purchase.preorder.payment_service.payment.processor;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.RedisKeyHelper;
import com.common.domain.common.PaymentPGName;
import com.common.domain.entity.payment.Payment;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.api.external.pg.TossPaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.dto.PaymentCompletionData;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentResponse;
import com.purchase.preorder.payment_service_common.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TossPaymentPGProcessor extends AbstractPaymentPGProcessor<TossPaymentConfirmRequest, TossPaymentCancelRequest> {

    private final TossPaymentsClient client;

    public TossPaymentPGProcessor(RedisService redisService, TossPaymentsClient client) {
        super(redisService);
        this.client = client;
    }

    @Override
    public boolean supports(String pgName) {
        return PaymentPGName.TOSS.getName().equals(pgName);
    }

    @Override
    public PaymentCompletionData confirm(Payment payment, TossPaymentConfirmRequest request) {
        TossPaymentResponse res = getOrRequest(RedisKeyHelper.tossKey(request.getOrderId()), TossPaymentResponse.class,
                () -> client.confirmPayment(request));
        if (!"DONE".equals(res.getStatus())) {
            log.warn("결제 실패 -> 결제 상태: {}", res.getStatus());
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }
        return PaymentCompletionData.mapFromTossResponse(res);
    }

    @Override
    public Class<TossPaymentConfirmRequest> getRequestType() {
        return TossPaymentConfirmRequest.class;
    }

    @Override
    public Class<TossPaymentCancelRequest> getCancelRequestType() {
        return TossPaymentCancelRequest.class;
    }

    @Override
    @Transactional
    public void cancel(Payment payment, String cancelReason) {
        TossPaymentCancelRequest request = TossPaymentCancelRequest.withReason(cancelReason).build();
        TossPaymentResponse response = client.cancelPayment(payment.getPgTransactionId(), request);
        if (!"DONE".equals(response.getCancels().getFirst().getCancelStatus())) {
            log.warn("결제 취소 실패 -> 결제 취소 상태: {}", response.getCancels().getFirst().getCancelStatus());
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }
    }
}
