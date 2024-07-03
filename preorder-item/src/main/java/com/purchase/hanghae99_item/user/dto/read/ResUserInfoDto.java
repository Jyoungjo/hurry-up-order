package com.purchase.hanghae99_item.user.dto.read;

import com.purchase.hanghae99_item.user.User;
import com.purchase.hanghae99_core.util.AesUtils;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUserInfoDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;

    public static ResUserInfoDto fromEntity(User user) throws Exception {
        return ResUserInfoDto.builder()
                .id(user.getId())
                .name(AesUtils.aesCBCDecode(user.getName()))
                .email(AesUtils.aesCBCDecode(user.getEmail()))
                .address(AesUtils.aesCBCDecode(user.getAddress()))
                .phoneNumber(AesUtils.aesCBCDecode(user.getPhoneNumber()))
                .build();
    }
}
