package com.purchase.preorder.user_service.user.dto.create;

import com.common.core.util.AesUtils;
import com.common.domain.common.UserRole;
import com.common.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqUserCreateDto {
    @NotBlank(message = "이메일은 필수 입력 대상입니다.")
//    @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식으로 입력해 주세요. (예: example@example.com)")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 대상입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~₩!@#$%^&*]).{7,19}$", message = "비밀번호는 8~20자의 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;
    @NotBlank(message = "실명은 필수 입력 대상입니다.")
    private String name;
    @NotBlank(message = "주소를 입력해주세요.")
    private String address;
    @NotBlank(message = "전화번호 입력은 필수 입력 대상입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "올바른 전화번호 형식으로 입력해 주세요. (예: 01012345678)")
    private String phoneNumber;

    public User toEntity(PasswordEncoder passwordEncoder) throws Exception {
        return User.builder()
                .name(AesUtils.aesCBCEncode(name))
                .email(AesUtils.aesCBCEncode(email))
                .password(passwordEncoder.encode(getPassword()))
                .address(AesUtils.aesCBCEncode(address))
                .phoneNumber(AesUtils.aesCBCEncode(phoneNumber))
                .role(UserRole.UNCERTIFIED_USER)
                .build();
    }
}
