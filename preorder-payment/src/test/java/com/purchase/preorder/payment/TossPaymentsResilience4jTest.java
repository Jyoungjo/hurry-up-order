package com.purchase.preorder.payment;

import com.common.domain.common.PaymentStatus;
import com.common.domain.entity.payment.Payment;
import com.common.domain.repository.payment.PaymentRepository;
import com.purchase.preorder.payment_service.api.external.pg.TossPaymentsClient;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentConfirmRequest;
import com.purchase.preorder.payment_service.api.external.pg.dto.toss.TossPaymentResponse;
import com.purchase.preorder.payment_service.payment.dto.ResPaymentDto;
import com.purchase.preorder.payment_service.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.cloud.gateway.enabled=false"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TossPaymentsResilience4jTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private TossPaymentsClient tossPaymentsClient;

    private TossPaymentConfirmRequest request;
    private Payment payment;

    @BeforeEach
    void setup() {
        request = new TossPaymentConfirmRequest("pay_abc", "ORD123", 10000);
        payment = Payment.builder()
                .id(1L)
                .pgOrderId("ORD123")
                .paymentPrice(10000)
                .paymentStatus(PaymentStatus.INITIATED)
                .build();
        paymentRepository.save(payment);
    }

    @Test
    @DisplayName("서킷브레이커 CLOSED 상태 정상 처리")
    void 서킷브레이커_CLOSED_성공() {
        // given
        TossPaymentResponse res = TossPaymentResponse.builder()
                .status("DONE")
                .orderId("ORD123")
                .requestedAt("2025-07-22T17:50:07.000+09:00")
                .approvedAt("2025-07-22T17:51:07.000+09:00")
                .totalAmount(10000)
                .paymentKey("pay_abc")
                .build();
        when(tossPaymentsClient.confirmPayment(any())).thenReturn(res);

        // when
        ResPaymentDto dto = paymentService.confirmPayment(request);

        // then
        assertEquals(1L, dto.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, dto.getStatus());
    }

    @Test
    @DisplayName("리트라이 후 성공하는 케이스")
    void 리트라이_후_성공() {
        // given
        TossPaymentResponse res = TossPaymentResponse.builder()
                .status("DONE")
                .orderId("ORD123")
                .requestedAt("2025-07-22T17:50:07.000+09:00")
                .approvedAt("2025-07-22T17:51:07.000+09:00")
                .totalAmount(10000)
                .paymentKey("pay_abc")
                .build();

        when(tossPaymentsClient.confirmPayment(any()))
                .thenThrow(new RuntimeException("일시적 장애"))
                .thenReturn(res);

        // when
        ResPaymentDto dto = paymentService.confirmPayment(request);

        // then
        assertEquals(1L, dto.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, dto.getStatus());
    }

    @Test
    @DisplayName("리트라이 최종 실패")
    void 리트라이_최종_실패() {
        // given
        when(tossPaymentsClient.confirmPayment(any())).thenThrow(new RuntimeException("장애 발생"));

        // when
        ResPaymentDto res = paymentService.confirmPayment(request);

        // when & then
        assertEquals(1L, res.getPaymentId());
        assertEquals(PaymentStatus.FAILED, res.getStatus());
    }
}
