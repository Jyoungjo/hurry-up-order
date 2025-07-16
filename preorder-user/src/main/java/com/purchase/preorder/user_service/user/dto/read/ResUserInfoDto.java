package com.purchase.preorder.user_service.user.dto.read;

import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
