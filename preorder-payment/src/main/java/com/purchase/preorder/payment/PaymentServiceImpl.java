package com.purchase.preorder.payment;

import com.purchase.preorder.dto.ReqPaymentDto;
import com.purchase.preorder.dto.ResPaymentDto;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public ResPaymentDto initiatePayment(ReqPaymentDto req) {
        Payment payment = paymentRepository.save(Payment.of(req));

        if (new Random().nextInt(100) < 20) {
            throw new BusinessException(ExceptionCode.CANCEL_PAYMENT);
        }

        return ResPaymentDto.fromEntity(payment);
    }

    @Transactional
    public ResPaymentDto completePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .filter(p -> p.getPaymentStatus().equals(PaymentStatus.INITIATED))
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_PAYMENT));

        if (new Random().nextInt(100) < 20) {
            throw new BusinessException(ExceptionCode.CANCEL_PAYMENT);
        }

        payment.completePayment();
        paymentRepository.save(payment);
        return ResPaymentDto.fromEntity(payment);
    }
}
