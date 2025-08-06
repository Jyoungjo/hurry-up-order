package com.common.event_common.domain_event_vo.payment;

import com.common.event_common.domain_event_vo.DomainEvent;
import com.common.event_common.domain_event_vo.DomainEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceedDomainEvent implements DomainEvent {
    private String eventId;
    private Long paymentId;
    private Long orderId;
    private String pgOrderId;
    private String pgName;
    private int totalAmount;
    private int feeAmount;
    private int settledAmount;
    private LocalDateTime soldAt;
    private LocalDateTime occurredAt;

    @Override
    public String getDomainEventType() {
        return DomainEventType.PAYMENT_SUCCEED.name();
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.orderId);
    }
}
