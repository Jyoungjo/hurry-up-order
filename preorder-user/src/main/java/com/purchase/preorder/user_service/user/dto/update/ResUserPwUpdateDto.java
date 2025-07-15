package com.purchase.preorder.user_service.user.dto.update;

import com.purchase.preorder.user.User;
import lombok.*;

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
