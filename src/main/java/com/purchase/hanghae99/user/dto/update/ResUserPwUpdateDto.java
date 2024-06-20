package com.purchase.hanghae99.user.dto.update;

import com.purchase.hanghae99.user.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResUserPwUpdateDto {
    private Long id;
    private String name;
    private String email;

    public static ResUserPwUpdateDto fromEntity(User user) {
        return ResUserPwUpdateDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
