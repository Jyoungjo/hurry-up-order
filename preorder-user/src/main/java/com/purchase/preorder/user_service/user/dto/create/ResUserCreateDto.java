package com.purchase.preorder.user_service.user.dto.create;

import com.common.domain.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUserCreateDto {
    private Long id;
    private String name;
    private String email;

    public static ResUserCreateDto fromEntity(User user) {
        return ResUserCreateDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
