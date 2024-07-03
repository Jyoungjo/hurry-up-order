package com.purchase.hanghae99_order.user.dto.update;

import com.purchase.hanghae99_order.user.User;
import lombok.*;

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
