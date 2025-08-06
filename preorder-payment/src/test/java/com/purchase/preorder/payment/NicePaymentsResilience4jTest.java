package com.purchase.preorder.payment;

import com.common.domain.common.PaymentStatus;
import com.common.domain.entity.payment.Payment;
import com.common.domain.repository.payment.PaymentRepository;
import com.purchase.preorder.payment_service.api.external.pg.NicePaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.TossPaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.nice.NicePaymentResponse;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.cloud.gateway.enabled=false"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NicePaymentsResilience4jTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private TossPaymentsClient tossPaymentsClient;

    @MockBean
    private NicePaymentsClient nicePaymentsClient;

    private NicePaymentRequest reqDto;
    private TossPaymentConfirmRequest tossRequest;
    private NicePaymentConfirmRequest niceRequest;
    private Payment payment;

    @BeforeEach
    void setup() {
        reqDto = new NicePaymentRequest("pay_abc", "ORD123", 10000);
        tossRequest = new TossPaymentConfirmRequest("pay_abc", "ORD123", 10000);
        niceRequest = new NicePaymentConfirmRequest(10000);
        payment = Payment.builder()
                .id(1L)
                .pgOrderId("ORD123")
                .paymentPrice(10000)
                .paymentStatus(PaymentStatus.INITIATED)
                .build();
        paymentRepository.save(payment);
    }

    @Test
    @DisplayName("서킷브레이커 OPEN 시 fallback 진입")
    void 서킷브레이커_OPEN_예외발생() throws InterruptedException {
        // 임의로 실패 상황 만들기 -> 서킷브레이커 OPEN
        when(tossPaymentsClient.confirmPayment(any())).thenThrow(new RuntimeException("Toss 장애"));

        CountDownLatch countDownLatch = new CountDownLatch(5);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        executorService.execute(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    paymentService.confirmPayment(tossRequest);
                } catch (Exception e) {
                    // 무시
                } finally {
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("confirmPayment");
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
    }

    @Test
    @DisplayName("서킷브레이커가 OPEN 상태일 때, 나이스 페이먼츠 정상 처리")
    void 나이스_페이먼츠_정상_처리() {
        // given
        NicePaymentResponse res = NicePaymentResponse.builder()
                .status("paid")
                .amount(10000)
                .ediDate("2025-07-22T17:50:07.000+0900")
                .paidAt("2025-07-22T17:51:07.000+0900")
                .tid("pay_abc")
                .build();

        when(nicePaymentsClient.confirmPayment(anyString(), any(NicePaymentConfirmRequest.class))).thenReturn(res);

        // when
        ResPaymentDto result = paymentService.confirmNicePayment(reqDto);

        // then
        assertEquals(1L, result.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    @DisplayName("리트라이 후 성공하는 케이스")
    void 리트라이_후_성공() {
        NicePaymentResponse res = NicePaymentResponse.builder()
                .status("paid")
                .amount(10000)
                .ediDate("2025-07-22T17:50:07.000+0900")
                .paidAt("2025-07-22T17:51:07.000+0900")
                .tid("pay_abc")
                .build();

        when(nicePaymentsClient.confirmPayment(anyString(), any(NicePaymentConfirmRequest.class)))
                .thenThrow(new RuntimeException("일시적 장애"))
                .thenReturn(res);

        ResPaymentDto dto = paymentService.confirmNicePayment(reqDto);

        assertEquals(1L, dto.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, dto.getStatus());
    }

    @Test
    @DisplayName("리트라이 최종 실패")
    void 리트라이_최종_실패() {
        // given
        when(nicePaymentsClient.confirmPayment(anyString(), any())).thenThrow(new RuntimeException("장애 발생"));

        // when
        ResPaymentDto res = paymentService.confirmNicePayment(reqDto);

        // when & then
        assertEquals(1L, res.getPaymentId());
        assertEquals(PaymentStatus.FAILED, res.getStatus());
    }
}
