package com.purchase.hanghae99_order.user.dto.create;

import com.purchase.hanghae99_order.user.User;
import lombok.*;

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
