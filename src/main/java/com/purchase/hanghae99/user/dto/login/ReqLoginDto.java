package com.purchase.hanghae99.user.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReqLoginDto {
    @NotBlank(message = "이메일은 필수 입력 대상입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 대상입니다.")
    private String password;
}
