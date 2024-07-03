package com.purchase.hanghae99_item.user.dto.login;

import com.purchase.hanghae99_item.user.User;
import lombok.*;

@Getter
@NoArgsConstructor
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
