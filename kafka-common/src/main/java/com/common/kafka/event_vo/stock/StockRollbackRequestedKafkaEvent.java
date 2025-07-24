package com.common.kafka.event_vo.stock;

import com.common.kafka.event_vo.KafkaEvent;
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
public class StockRollbackRequestedKafkaEvent implements KafkaEvent {

    private String topic;
    private String aggregateId;
    private String eventId;
    private String eventType;
    private Long orderId;
    private Map<Long, Integer> stockMap;
    private LocalDateTime occurredAt;
}
