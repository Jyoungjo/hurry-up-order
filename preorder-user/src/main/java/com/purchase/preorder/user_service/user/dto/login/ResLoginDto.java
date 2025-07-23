package com.purchase.preorder.user_service.user.dto.login;

import com.common.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
