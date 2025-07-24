package com.purchase.preorder.user_service.user.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUserPwUpdateDto {
    private static final String MESSAGE = "비밀번호가 변경되었습니다.";

    private Long id;
    private String message;

    public static ResUserPwUpdateDto fromEntity(Long userId) {
        return ResUserPwUpdateDto.builder()
                .id(userId)
                .message(MESSAGE)
                .build();
    }
}
