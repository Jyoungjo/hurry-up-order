package com.purchase.preorder.user_service.email;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ResEmailDto {
    private static final String SUCCESS_MESSAGE = "인증 메일이 전송되었습니다.";

    private final String email;
    private final String message;

    public static ResEmailDto of(String email) {
        return ResEmailDto.builder()
                .email(email)
                .message(SUCCESS_MESSAGE)
                .build();
    }
}
