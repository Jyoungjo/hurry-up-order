package com.purchase.preorder.item_service_common.event_fail;

import com.common.domain.entity.common.EventFailure;
import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.handler.EventFailureHandler;
import com.common.kafka.event_vo.KafkaEvent;
import com.common.kafka.fail.EventFailureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemEventFailureService implements EventFailureService {

    private final EventFailureHandler<DomainEvent> domainHandler;
    private final EventFailureHandler<KafkaEvent> kafkaHandler;

    @Override
    public void saveEventFailure(DomainEvent domainEvent, Throwable ex) {
        domainHandler.saveFailure(domainEvent, ex, EventFailure.EventFailureCategory.DOMAIN);
    }

    @Override
    public void saveEventFailure(KafkaEvent kafkaEvent, Throwable ex) {
        kafkaHandler.saveFailure(kafkaEvent, ex, EventFailure.EventFailureCategory.KAFKA);
    }
}
