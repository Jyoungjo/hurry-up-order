package com.purchase.preorder.item_service_common.event_fail;

import com.common.domain.entity.common.EventFailure;
import com.common.domain.repository.common.EventFailureRepository;
import com.common.kafka.handler.KafkaEventFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemKafkaEventFailureHandler extends KafkaEventFailureHandler {

    private final EventFailureRepository repository;

    @Override
    protected final void doSave(EventFailure eventFailure) {
        repository.save(eventFailure);
    }
}
