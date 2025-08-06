package com.purchase.preorder.email_service.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResEmailDto {
    private static final String SUCCESS_MESSAGE = "이메일 인증이 완료되었습니다.";

    private final String email;
    private final String message;

    public static ResEmailDto of(String email) {
        return ResEmailDto.builder()
                .email(email)
                .message(SUCCESS_MESSAGE)
                .build();
    }
}
