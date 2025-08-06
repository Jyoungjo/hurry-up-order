package com.purchase.preorder.payment_service.payment.processor;

import com.common.domain.common.PaymentPGName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentPGProcessorRegistry {
    private final List<PaymentPGProcessor<?, ?>> processors;

    @SuppressWarnings("unchecked")
    public <T, C> PaymentPGProcessor<T, C> getProcessor(PaymentPGName pgName, Class<T> confirmType, Class<C> cancelType) {
        return (PaymentPGProcessor<T, C>) processors.stream()
                .filter(p ->
                        p.supports(pgName.getName())
                                && p.getRequestType().equals(confirmType)
                                && p.getCancelRequestType().equals(cancelType)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 PG"));
    }
}
