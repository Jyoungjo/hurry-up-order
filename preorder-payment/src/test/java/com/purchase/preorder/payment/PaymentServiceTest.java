package com.purchase.preorder.payment;

import com.purchase.preorder.payment_service.dto.ReqPaymentDto;
import com.purchase.preorder.payment_service.dto.ResPaymentDto;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.payment_service.payment.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static com.purchase.preorder.exception.ExceptionCode.CANCEL_PAYMENT;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_PAYMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Random random;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void init() {
        payment = Payment.builder()
                .id(1L)
                .orderId(1L)
                .paymentDate(LocalDateTime.now())
                .paymentPrice(10000)
                .paymentStatus(PaymentStatus.INITIATED)
                .deletedAt(null)
                .build();
    }

    // CREATE
    @DisplayName("결제 시도 기능 성공")
    @Test
    void 결제_시도_기능_성공() {
        // given
        ReqPaymentDto req = new ReqPaymentDto(1L, 10000);
        when(random.nextInt(anyInt())).thenReturn(100);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        ResPaymentDto res = paymentService.initiatePayment(req);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
    }

    // CREATE
    @DisplayName("결제 시도 기능 실패 - 사용자 변심(20% 확률)으로 실패")
    @Test
    void 결제_시도_기능_실패_사용자_변심() {
        // given
        ReqPaymentDto req = new ReqPaymentDto(1L, 10000);
        when(random.nextInt(anyInt())).thenReturn(10);
        // when

        // then
        assertThatThrownBy(() -> paymentService.initiatePayment(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CANCEL_PAYMENT.getMessage());
    }

    // UPDATE
    @DisplayName("결제 완료 기능 성공")
    @Test
    void 결제_완료_기능_성공() {
        // given
        Long paymentId = 1L;
        PaymentStatus status = payment.getPaymentStatus();
        when(random.nextInt(anyInt())).thenReturn(100);
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        Payment newPayment = Payment.builder()
                .id(1L)
                .orderId(1L)
                .paymentDate(LocalDateTime.now())
                .paymentPrice(10000)
                .paymentStatus(PaymentStatus.COMPLETED)
                .deletedAt(null)
                .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(newPayment);

        // when
        ResPaymentDto res = paymentService.completePayment(paymentId);

        // then
        assertThat(res.getPaymentId()).isEqualTo(payment.getId());
        assertThat(newPayment.getPaymentStatus()).isNotEqualTo(status);
        assertThat(newPayment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    // UPDATE
    @DisplayName("결제 완료 기능 실패 - 사용자 변심(20% 확률)으로 실패")
    @Test
    void 결제_완료_기능_실패_사용자_변심() {
        // given
        Long paymentId = 1L;
        when(random.nextInt(anyInt())).thenReturn(10);

        // when

        // then
        assertThatThrownBy(() -> paymentService.completePayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CANCEL_PAYMENT.getMessage());
    }

    // UPDATE
    @DisplayName("결제 완료 기능 실패 - 존재하지 않는 결제 정보")
    @Test
    void 결제_완료_기능_실패_존재하지_않는_결제_정보() {
        // given
        Long paymentId = 1L;
        when(random.nextInt(anyInt())).thenReturn(100);

        // when
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> paymentService.completePayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_PAYMENT.getMessage());
    }
}
