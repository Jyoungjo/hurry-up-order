package com.purchase.preorder.settlement_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.payment_service.event.PaymentSucceedEvent;
import com.common.domain.entity.EventFailure;
import com.common.domain.repository.EventFailureRepository;
import com.purchase.preorder.settlement_service.settlement.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSucceedEventListener {
    private static final String SERIALIZE_ERROR = "serialization_failed";

    private final SettlementService settlementService;
    private final EventFailureRepository eventFailureRepository;

    @Async(value = "eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentSucceedEvent event) {
        log.info("이벤트 수신 완료");

        try {
            settlementService.create(event);
            log.info("정산 엔티티 저장 완료");
        } catch (Exception e) {
            log.error("정산 저장 실패 - {}", e.getMessage());

            EventFailure eventFailure = EventFailure.of(
                    event.getClass().getSimpleName(),
                    serializeEvent(event),
                    e.getMessage()
            );

            eventFailureRepository.save(eventFailure);
        }
    }

    private String serializeEvent(Object event) {
        try {
            return new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return SERIALIZE_ERROR;
        }
    }
}
