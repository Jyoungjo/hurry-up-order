package com.purchase.preorder.payment;

import com.common.domain.common.PaymentStatus;
import com.common.domain.entity.payment.Payment;
import com.common.domain.repository.payment.PaymentRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.api.external.pg.NicePaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.TossPaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentResponse;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentResponse;
import com.purchase.preorder.payment_service.payment.dto.ReqPaymentInitiateDto;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.payment.service.PaymentServiceImpl;
import com.purchase.preorder.payment_service_common.util.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.common.core.exception.ExceptionCode.NOT_FOUND_PAYMENT;
import static com.common.core.exception.ExceptionCode.SERVICE_UNAVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TossPaymentsClient tossPaymentsClient;

    @Mock
    private NicePaymentsClient nicePaymentsClient;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private TossPaymentConfirmRequest tossRequest;
    private TossPaymentResponse tossResponse;
    private NicePaymentRequest niceReq;
    private NicePaymentConfirmRequest niceRequest;
    private NicePaymentResponse niceResponse;

    @BeforeEach
    void init() {
        tossRequest = new TossPaymentConfirmRequest("paymentKey", "order123", 10000);
        tossResponse = TossPaymentResponse.builder()
                .orderId("order123")
                .paymentKey("paymentKey")
                .requestedAt("2025-07-22T18:33:09.000+09:00")
                .approvedAt("2025-07-22T18:33:10.000+09:00")
                .totalAmount(10000)
                .mId("testMid")
                .status("DONE")
                .build();
        niceReq = new NicePaymentRequest("paymentKey", "order123", 10000);
        niceRequest = new NicePaymentConfirmRequest(10000);
        niceResponse = NicePaymentResponse.builder()
                .orderId("order123")
                .tid("paymentKey")
                .ediDate("2025-07-22T18:33:09.000+0900")
                .paidAt("2025-07-22T18:33:10.000+0900")
                .amount(10000)
                .status("paid")
                .build();

        payment = Payment.of(1L, 10000, "order123", "uuid-key");
    }

    // CREATE
    @DisplayName("결제 시도 기능 성공")
    @Test
    void 결제_시도_기능_성공() {
        // given
        ReqPaymentInitiateDto req = new ReqPaymentInitiateDto(1L, "order123", 10000);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        Long res = paymentService.initiatePayment(req);

        // then
        assertThat(res).isEqualTo(payment.getId());
    }

    // UPDATE
    @DisplayName("토스 결제 확인 기능 성공")
    @Test
    void 토스_결제_확인_기능_성공() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));
        when(redisService.getValues(anyString(), eq(TossPaymentResponse.class))).thenReturn(null);
        when(tossPaymentsClient.confirmPayment(any(TossPaymentConfirmRequest.class))).thenReturn(tossResponse);
        doNothing().when(redisService).setValues(anyString(), any(TossPaymentResponse.class));

        // when
        ResPaymentDto res = paymentService.confirmPayment(tossRequest);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    // UPDATE
    @DisplayName("토스 결제 확인 기능 실패 - Payment 존재X")
    @Test
    void 토스_결제_확인_기능_실패_엔티티_존재X() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> paymentService.confirmPayment(tossRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_PAYMENT.getMessage());
    }

    // UPDATE
    @DisplayName("토스 결제 확인 기능 성공 - Redis 캐시")
    @Test
    void 토스_결제_확인_기능_성공_Redis_캐시() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));
        when(redisService.getValues(anyString(), eq(TossPaymentResponse.class))).thenReturn(tossResponse);

        // when
        ResPaymentDto res = paymentService.confirmPayment(tossRequest);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    // UPDATE
    @DisplayName("토스 결제 확인 서킷브레이커 OPEN")
    @Test
    void 토스_결제_확인_서킷브레이커_OPEN() {
        // given
        Throwable cause = new RuntimeException("서킷브레이커 열림");

        // when

        // then
        assertThatThrownBy(() -> paymentService.onCircuitBreakerOpen(tossRequest, cause))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(SERVICE_UNAVAILABLE.getMessage());
    }

    // UPDATE
    @DisplayName("토스 결제 확인 리트라이 최종 실패")
    @Test
    void 토스_결제_확인_리트라이_최종_실패() {
        // given
        Throwable t = new RuntimeException("리트라이 최종 실패");
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));

        // when
        ResPaymentDto res = paymentService.onTossConfirmFailure(tossRequest, t);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    // UPDATE
    @DisplayName("토스 리트라이 최종 실패 - 엔티티 존재 X")
    @Test
    void 토스_리트라이_최종_실패_엔티티_존재_X() {
        // given
        Throwable t = new RuntimeException("리트라이 최종 실패");
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> paymentService.onTossConfirmFailure(tossRequest, t))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_PAYMENT.getMessage());
    }

    // UPDATE
    @DisplayName("나이스 결제 확인 기능 성공")
    @Test
    void 나이스_결제_확인_기능_성공() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));
        when(redisService.getValues(anyString(), eq(NicePaymentResponse.class))).thenReturn(null);
        when(nicePaymentsClient.confirmPayment(anyString(), any(NicePaymentConfirmRequest.class))).thenReturn(niceResponse);
        doNothing().when(redisService).setValues(anyString(), any(NicePaymentResponse.class));

        // when
        ResPaymentDto res = paymentService.confirmNicePayment(niceReq);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    // UPDATE
    @DisplayName("나이스 결제 확인 기능 실패 - Payment 존재X")
    @Test
    void 나이스_결제_확인_기능_실패_엔티티_존재X() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> paymentService.confirmNicePayment(niceReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_PAYMENT.getMessage());
    }

    // UPDATE
    @DisplayName("나이스 결제 확인 기능 성공 - Redis 캐시")
    @Test
    void 나이스_결제_확인_기능_성공_Redis_캐시() {
        // given
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));
        when(redisService.getValues(anyString(), eq(NicePaymentResponse.class))).thenReturn(niceResponse);

        // when
        ResPaymentDto res = paymentService.confirmNicePayment(niceReq);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    // UPDATE
    @DisplayName("나이스 결제 확인 리트라이 최종 실패")
    @Test
    void 나이스_결제_확인_리트라이_최종_실패() {
        // given
        Throwable t = new RuntimeException("리트라이 최종 실패");

        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.of(payment));

        // when
        ResPaymentDto res = paymentService.onNiceConfirmFailure(niceReq, t);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    // UPDATE
    @DisplayName("나이스 리트라이 최종 실패 - 엔티티 존재 X")
    @Test
    void 나이스_리트라이_최종_실패_엔티티_존재_X() {
        // given
        Throwable t = new RuntimeException("리트라이 최종 실패");
        when(paymentRepository.findByPgOrderId(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> paymentService.onNiceConfirmFailure(niceReq, t))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_PAYMENT.getMessage());
    }
}
