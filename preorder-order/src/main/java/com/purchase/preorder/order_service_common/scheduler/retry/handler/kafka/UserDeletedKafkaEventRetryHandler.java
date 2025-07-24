package com.purchase.preorder.order_service_common.scheduler.retry.handler.kafka;

import com.common.event_common.handler.EventRetryHandler;
import com.common.kafka.constant.KafkaEventType;
import com.common.kafka.event_vo.user.UserDeletedKafkaEvent;
import com.purchase.preorder.order_service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletedKafkaEventRetryHandler implements EventRetryHandler<UserDeletedKafkaEvent> {

    private final OrderService orderService;

    @Override
    public String getEventType() {
        return KafkaEventType.USER_DELETED;
    }

    @Override
    public void handle(UserDeletedKafkaEvent event) throws Exception {
        orderService.deleteOrder(event.getUserId());
    }

    @Override
    public Class<UserDeletedKafkaEvent> getEventClass() {
        return UserDeletedKafkaEvent.class;
    }
}
