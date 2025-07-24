package com.purchase.preorder.user_service.user.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqLoginDto {
    @NotBlank(message = "이메일은 필수 입력 대상입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 대상입니다.")
    private String password;
}
