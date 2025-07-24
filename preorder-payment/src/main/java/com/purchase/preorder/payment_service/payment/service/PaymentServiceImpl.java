package com.purchase.preorder.payment_service.payment.service;

import com.common.core.exception.ExceptionCode;
import com.common.domain.common.PaymentPGName;
import com.common.domain.entity.payment.Payment;
import com.common.domain.entity.payment.PaymentIdOnly;
import com.common.domain.repository.payment.PaymentRepository;
import com.common.event_common.domain_event_vo.payment.*;
import com.common.event_common.mapper.PaymentDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.common.kafka.fail.EventFailureService;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.api.external.pg.dto.PaymentCompletionData;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentCancelRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.payment.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.payment.processor.PaymentPGProcessor;
import com.purchase.preorder.payment_service.payment.processor.PaymentPGProcessorRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentPGProcessorRegistry processorRegistry;
    private final DomainEventPublisher eventPublisher;
    private final PaymentDomainEventMapper mapper;
    private final EventFailureService eventFailureService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentPGProcessorRegistry processorRegistry,
                              @Qualifier("paymentDomainEventPublisher") DomainEventPublisher eventPublisher,
                              PaymentDomainEventMapper mapper,
                              EventFailureService eventFailureService) {
        this.paymentRepository = paymentRepository;
        this.processorRegistry = processorRegistry;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
        this.eventFailureService = eventFailureService;
    }

    @Override
    @Transactional
    public Long initiatePayment(ReqPaymentInitiateDto req) {
        /*
         * orderId의 경우 DB에 저장되는 orderId와 구분하기 위해 프론트에서 난수 생성 후 넘겨줌
         * 프론트단에서 만드는 paymentKey는 UUID로 대체
         */
        Payment payment = Payment.of(
                req.getOrdId(),
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

        PaymentPGProcessor<TossPaymentConfirmRequest, TossPaymentCancelRequest> processor =
                processorRegistry.getProcessor(PaymentPGName.TOSS, TossPaymentConfirmRequest.class, TossPaymentCancelRequest.class);
        PaymentCompletionData data = processor.confirm(payment, req);
        tryCompletePayment(payment, data);

        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }

    @Override
    @Transactional
    @Retry(name = "confirmNicePayment", fallbackMethod = "onNiceConfirmFailure")
    public ResPaymentDto confirmNicePayment(NicePaymentRequest req) {
        log.info("Nice 결제 시도 - tid: {}", req.getTid());
        Payment payment = getPaymentOrThrow(req.getOrderId());

        PaymentPGProcessor<NicePaymentRequest, NicePaymentCancelRequest> processor =
                processorRegistry.getProcessor(PaymentPGName.NICE, NicePaymentRequest.class, NicePaymentCancelRequest.class);
        PaymentCompletionData data = processor.confirm(payment, req);
        tryCompletePayment(payment, data);

        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public void cancelPaymentByCancel(Long orderId, String cancelReason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));

        cancel(payment, cancelReason);
        PaymentCanceledByCancelDomainEvent event = mapper.toPaymentCanceledByCancelEvent(payment.getId(), payment.getOrderId(), cancelReason);
        eventPublisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional(readOnly = true)
    public void cancelPaymentByRollback(Long orderId, String cancelReason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));

        cancel(payment, cancelReason);
        PaymentCanceledByRollbackDomainEvent event = mapper.toPaymentCanceledByRollbackEvent(payment.getId(), payment.getOrderId(), cancelReason);
        eventPublisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional(readOnly = true)
    public void cancelPaymentByReturn(Long shipmentId, Long orderId, String cancelReason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));

        cancel(payment, cancelReason);
        PaymentCanceledByReturnDomainEvent event = mapper.toPaymentCanceledByReturnEvent(shipmentId, payment.getId(), payment.getOrderId(), cancelReason);
        eventPublisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional
    public void delete(Long orderId) {
        PaymentIdOnly pio = paymentRepository.findPaymentIdOnlyByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));

        paymentRepository.deleteByOrderId(LocalDateTime.now(), orderId);

        PaymentDeletedDomainEvent event = mapper.toPaymentDeletedEvent(pio.getId());
        eventPublisher.publishOnlySpringEventAfterCommit(event);
    }

    public ResPaymentDto onCircuitBreakerOpen(TossPaymentConfirmRequest req, CallNotPermittedException t) {
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
            PaymentSucceedDomainEvent event = mapper.toPaymentSucceedEvent(payment);
            eventPublisher.publishWithOutboxAfterCommit(event);
        } catch (Exception e) {
            log.warn("결제 응답 성공, DB 저장 실패", e);
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ResPaymentDto failAndBuildResponse(String orderId, String paymentRef) {
        log.error("최종 결제 실패 - 주문 ID: {}, PG 참조: {}", orderId, paymentRef);
        Payment payment = getPaymentOrThrow(orderId);
        payment.failPayment(orderId, paymentRef);

        PaymentFailureDomainEvent event = mapper.toPaymentFailureEvent(payment.getOrderId());
        eventPublisher.publishWithOutboxAfterCommit(event);

        return ResPaymentDto.of(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }

    private void cancel(Payment payment, String cancelReason) {
        PaymentPGProcessor<?, ?> processor;
        if (payment.getPgName().equals(PaymentPGName.TOSS.getName())) {
            processor = processorRegistry.getProcessor(PaymentPGName.TOSS, TossPaymentConfirmRequest.class, TossPaymentCancelRequest.class);
        } else {
            processor = processorRegistry.getProcessor(PaymentPGName.NICE, NicePaymentRequest.class, NicePaymentCancelRequest.class);
        }

        processor.cancel(payment, cancelReason);
    }
}
