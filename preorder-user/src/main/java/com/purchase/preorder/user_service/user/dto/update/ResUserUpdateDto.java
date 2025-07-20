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
public class ResUserUpdateDto {
    private Long id;
    private String address;
    private String phoneNumber;

    public static ResUserUpdateDto fromEntity(User user) {
        return ResUserUpdateDto.builder()
                .id(user.getId())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
