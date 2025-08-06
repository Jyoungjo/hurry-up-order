package com.common.domain.entity.user.projection;

import java.time.LocalDateTime;

public interface LoginInfo {
    Long getId();
    String getName();
    String getPassword();
    String getEmail();
    LocalDateTime getEmailVerifiedAt();
}
