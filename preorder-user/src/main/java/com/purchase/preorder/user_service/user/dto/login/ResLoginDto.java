package com.purchase.preorder.user_service.user.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResLoginDto {
    private static final String MESSAGE = "로그인 성공!";

    private String name;
    private String email;
    private String message;

    public static ResLoginDto of(String name, String email) {
        return ResLoginDto.builder()
                .name(name)
                .email(email)
                .message(MESSAGE)
                .build();
    }
}
