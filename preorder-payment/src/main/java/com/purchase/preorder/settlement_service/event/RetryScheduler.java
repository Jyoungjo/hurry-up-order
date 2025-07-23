package com.purchase.preorder.settlement_service.event;

import com.common.domain.entity.EventFailure;
import com.common.domain.repository.EventFailureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.payment_service.event.PaymentSucceedEvent;
import com.purchase.preorder.settlement_service.settlement.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final EventFailureRepository eventFailureRepository;
    private final SettlementService settlementService;

    @Scheduled(fixedDelay = 60000) // 1분마다
    @Transactional
    public void retryFailedEvents() {
        List<EventFailure> failedEvents = eventFailureRepository.findTop100ByProcessedFalseAndRetryCountLessThan(5);

        for (EventFailure failure : failedEvents) {
            try {
                // 복원 및 재처리
                PaymentSucceedEvent event = new ObjectMapper().readValue(failure.getPayload(), PaymentSucceedEvent.class);
                settlementService.create(event);

                failure.success();
            } catch (Exception e) {
                failure.fail(e.getMessage());
            }
        }
    }
}
