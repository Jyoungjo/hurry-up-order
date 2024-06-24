package com.purchase.hanghae99.user.dto.login;

import com.purchase.hanghae99.user.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResLoginDto {
    private String name;
    private String email;
    private String message;

    public static ResLoginDto fromEntity(User user) {
        return ResLoginDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .message("로그인 성공!")
                .build();
    }
}
