package com.common.kafka.event_vo.payment;

import com.common.domain.common.OrderStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum CancelReason {
    RETURN("반품"),
    CANCEL("취소"),
    INTERNAL_SERVER_ISSUE("내부 서버 이슈");

    private final String label;

    @JsonCreator
    public static CancelReason from(String value) {
        return Arrays.stream(values())
                .filter(r -> r.label.equals(value))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("Unknown cancel reason received: {}, defaulting to CANCEL", value);
                    return CANCEL;
                });
    }

    public OrderStatus toOrderStatus() {
        return switch (this) {
            case RETURN -> OrderStatus.RETURNED;
            case CANCEL -> OrderStatus.CANCELED;
            default -> OrderStatus.CANCELED;
        };
    }
}
