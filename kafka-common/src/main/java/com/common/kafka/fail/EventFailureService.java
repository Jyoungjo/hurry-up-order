package com.common.kafka.fail;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.kafka.event_vo.KafkaEvent;

public interface EventFailureService {
    void saveEventFailure(DomainEvent domainEvent, Throwable ex);
    void saveEventFailure(KafkaEvent kafkaEvent, Throwable ex);
}
