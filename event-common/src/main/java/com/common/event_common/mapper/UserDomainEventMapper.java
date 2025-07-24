package com.common.event_common.mapper;

import com.common.core.util.AesUtils;
import com.common.domain.entity.user.User;
import com.common.event_common.domain_event_vo.user.UserCreatedDomainEvent;
import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserDomainEventMapper {

    public UserCreatedDomainEvent toUserCreatedEvent(User user) throws Exception {
        return UserCreatedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(AesUtils.aesCBCDecode(user.getEmail()))
                .occurredAt(LocalDateTime.now())
                .build();
    }

    public UserDeletedDomainEvent toUserDeletedEvent(Long userId) {
        return UserDeletedDomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
