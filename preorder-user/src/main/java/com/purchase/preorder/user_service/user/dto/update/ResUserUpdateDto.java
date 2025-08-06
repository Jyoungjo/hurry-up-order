package com.purchase.preorder.user_service.user.dto.update;

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

    public static ResUserUpdateDto of(Long userId, String address, String phoneNumber) {
        return ResUserUpdateDto.builder()
                .id(userId)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }
}
