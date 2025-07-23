package com.purchase.preorder.payment_service.payment;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.RedisKeyHelper;
import com.common.domain.entity.Payment;
import com.common.domain.repository.PaymentRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.api.external.NicePaymentsClient;
import com.purchase.preorder.payment_service.api.external.TossPaymentsClient;
import com.purchase.preorder.payment_service.common.RedisService;
import com.purchase.preorder.payment_service.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final NicePaymentsClient nicePaymentsClient;
    private final RedisService redisService;

    @Override
    @Transactional
    public Long initiatePayment(ReqPaymentInitiateDto req) {
        // TODO 추후 이벤트 처리시 엔티티 생성 성공 이벤트 발급 -> 결제 시도 하도록
        /*
         * orderId의 경우 DB에 저장되는 orderId와 구분하기 위해 프론트에서 난수 생성 후 넘겨줌
         * 프론트단에서 만드는 paymentKey는 UUID로 대체
         */
        Payment payment = Payment.of(
                req.getAmount(),
                req.getOrderId(),
                UUID.randomUUID().toString());

        return paymentRepository.save(payment).getId();
    }

    @Override
    @Transactional
    @Retry(name = "confirmPayment", fallbackMethod = "onTossConfirmFailure")
    @CircuitBreaker(name = "confirmPayment", fallbackMethod = "onCircuitBreakerOpen")
    public ResPaymentDto confirmPayment(TossPaymentConfirmRequest req) {
        log.info("Toss 결제 시도 - PG order id: {}", req.getOrderId());
        Payment payment = getPaymentOrThrow(req.getOrderId());

        TossPaymentConfirmResponse response = getOrRequest(
                RedisKeyHelper.tossKey(req.getOrderId()),
                TossPaymentConfirmResponse.class,
                () -> tossPaymentsClient.confirmPayment(req)
        );

        if ("DONE".equals(response.getStatus())) {
            tryCompletePayment(payment, PaymentCompletionData.mapFromTossResponse(response));
        }

        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }

    @Override
    @Transactional
    @Retry(name = "confirmNicePayment", fallbackMethod = "onNiceConfirmFailure")
    public ResPaymentDto confirmNicePayment(NicePaymentRequest req) {
        log.info("Nice 결제 시도 - tid: {}", req.getTid());
        Payment payment = getPaymentOrThrow(req.getOrderId());

        NicePaymentConfirmResponse response = getOrRequest(
                RedisKeyHelper.niceKey(req.getOrderId()),
                NicePaymentConfirmResponse.class,
                () -> nicePaymentsClient.confirmPayment(req.getTid(), NicePaymentConfirmRequest.of(req.getAmount()))
        );

        if ("paid".equals(response.getStatus())) {
            tryCompletePayment(payment, PaymentCompletionData.mapFromNiceResponse(response));
        }

        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }

    public ResPaymentDto onCircuitBreakerOpen(TossPaymentConfirmRequest req, Throwable t) {
        log.warn("Toss 서킷브레이커 OPEN - orderId: {}, 원인: {}", req.getOrderId(), t.getMessage());
        throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
    }

    @Transactional
    public ResPaymentDto onTossConfirmFailure(TossPaymentConfirmRequest req, Throwable t) {
        return failAndBuildResponse(req.getOrderId(), req.getPaymentKey());
    }

    @Transactional
    public ResPaymentDto onNiceConfirmFailure(NicePaymentRequest req, Throwable t) {
        return failAndBuildResponse(req.getOrderId(), req.getTid());
    }

    private Payment getPaymentOrThrow(String orderId) {
        return paymentRepository.findByPgOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));
    }

    private <T> T getOrRequest(String redisKey, Class<T> clazz, Supplier<T> fetcher) {
        T cached = redisService.getValues(redisKey, clazz);
        if (cached != null) {
            log.info("캐시된 PG 결과 사용 - key: {}", redisKey);
            return cached;
        }

        T response = fetcher.get();
        redisService.setValues(redisKey, response);
        return response;
    }

    private void tryCompletePayment(Payment payment, PaymentCompletionData data) {
        try {
            payment.completePayment(
                    data.getOrderId(),
                    data.getPgSource(),
                    data.getRequestedAt(),
                    data.getApprovedAt(),
                    data.getAmount(),
                    data.getTransactionId()
            );
        } catch (Exception e) {
            log.warn("결제 응답 성공, DB 저장 실패", e);
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ResPaymentDto failAndBuildResponse(String orderId, String paymentRef) {
        log.error("최종 결제 실패 - 주문 ID: {}, PG 참조: {}", orderId, paymentRef);
        Payment payment = getPaymentOrThrow(orderId);
        payment.failPayment(orderId, paymentRef);
        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }
}
