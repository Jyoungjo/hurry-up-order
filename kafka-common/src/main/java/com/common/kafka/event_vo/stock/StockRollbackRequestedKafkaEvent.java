package com.common.kafka.event_vo.stock;

import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.KafkaEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName(KafkaEventType.STOCK_ROLLBACK_REQUESTED)
public class StockRollbackRequestedKafkaEvent implements KafkaEvent {

    private String topic;
    private String aggregateId;
    private String eventId;
    private String eventType;
    private Long orderId;
    private Map<Long, Integer> stockMap;
    private LocalDateTime occurredAt;
}
