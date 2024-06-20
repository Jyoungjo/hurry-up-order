package com.purchase.hanghae99.user.dto.read;

import com.purchase.hanghae99.user.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResUserInfoDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;

    public static ResUserInfoDto fromEntity(User user) {
        return ResUserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
