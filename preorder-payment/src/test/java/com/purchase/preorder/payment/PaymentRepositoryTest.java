package com.purchase.preorder.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment() {
        return Payment.builder()
                .id(1L)
                .orderId(1L)
                .paymentDate(LocalDateTime.now())
                .paymentPrice(10000)
                .paymentStatus(PaymentStatus.INITIATED)
                .deletedAt(null)
                .build();
    }

    // CREATE
    @DisplayName("결제 정보 생성 성공")
    @Test
    void 결제_정보_생성() {
        // given
        Payment payment = payment();

        // when
        Payment savedPayment = paymentRepository.save(payment);

        // then
        assertThat(savedPayment.getPaymentPrice()).isEqualTo(payment.getPaymentPrice());
    }

    // UPDATE
    @DisplayName("결제 정보 수정 성공")
    @Test
    void 결제_정보_수정() {
        // given
        Payment savedPayment = paymentRepository.save(payment());
        PaymentStatus status = savedPayment.getPaymentStatus();

        // when
        savedPayment.completePayment();
        Payment updatedPayment = paymentRepository.save(savedPayment);

        // then
        assertThat(updatedPayment.getPaymentStatus()).isNotEqualTo(status);
        assertThat(updatedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
