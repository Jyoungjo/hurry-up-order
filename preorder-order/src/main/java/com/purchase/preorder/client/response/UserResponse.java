package com.purchase.preorder.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime emailVerifiedAt;
    private String password;
    private String address;
    private String phoneNumber;
    private String role;
    private LocalDateTime deletedAt;
}
