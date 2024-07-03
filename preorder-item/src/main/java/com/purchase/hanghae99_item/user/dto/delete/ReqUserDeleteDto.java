package com.purchase.hanghae99_item.user.dto.delete;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUserDeleteDto {
    @NotBlank(message = "비밀번호는 필수 입력 대상입니다.")
    private String password;
    @NotBlank(message = "비밀번호는 필수 입력 대상입니다.")
    private String password2;
}
