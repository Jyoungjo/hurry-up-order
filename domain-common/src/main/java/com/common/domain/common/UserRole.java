package com.common.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    UNCERTIFIED_USER("미인증 유저"), CERTIFIED_USER("인증 유저"), ADMIN("관리자");

    private final String role;
}
