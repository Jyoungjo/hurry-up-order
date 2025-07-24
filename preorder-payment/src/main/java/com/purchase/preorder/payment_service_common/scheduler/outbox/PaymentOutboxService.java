package com.purchase.preorder.payment_service_common.scheduler.outbox;

import com.common.core.exception.ExceptionCode;
import com.common.domain.entity.common.OutboxMessage;
import com.common.domain.repository.common.OutboxRepository;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.outbox.AbstractOutboxService;
import com.common.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentOutboxService extends AbstractOutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    protected final OutboxMessage getOutBox(KafkaEvent kafkaEvent) {
        return outboxRepository.findByEventId(kafkaEvent.getEventId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_OUTBOX));
    }

    @Override
    protected final void save(OutboxMessage outbox) {
        outboxRepository.save(outbox);
    }

    @Override
    protected final List<OutboxMessage> getTop100ByUnpublishedList() {
        return outboxRepository.findTop100ByPublishedFalseAndDlqFalseOrderByCreatedAtAsc();
    }
}
