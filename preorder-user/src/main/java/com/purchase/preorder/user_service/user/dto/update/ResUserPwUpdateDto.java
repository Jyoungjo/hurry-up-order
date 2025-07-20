package com.purchase.preorder.user_service.user.dto.update;

import com.common.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
