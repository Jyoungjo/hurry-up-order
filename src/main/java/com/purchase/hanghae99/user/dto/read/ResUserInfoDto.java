package com.purchase.hanghae99.user.dto.read;

import com.purchase.hanghae99.user.User;
import lombok.*;

import static com.purchase.hanghae99.common.AesUtils.*;

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
                .name(aesCBCDecode(user.getName()))
                .email(aesCBCDecode(user.getEmail()))
                .address(aesCBCDecode(user.getAddress()))
                .phoneNumber(aesCBCDecode(user.getPhoneNumber()))
                .build();
    }
}
