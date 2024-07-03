package com.purchase.hanghae99_order.common.security;

import com.purchase.hanghae99_order.user.User;
import com.purchase.hanghae99_order.user.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime emailVerifiedAt;
    private String password;
    private String address;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime deletedAt;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    public CustomUserDetails(String email, UserRole role) {
        this.email = email;
        this.role = role;
    }

    public static CustomUserDetails of(User user) {
        return new CustomUserDetails(user);
    }

    public static CustomUserDetails of(String username, UserRole role) {
        return new CustomUserDetails(username, role);
    }

    public User getInstance() {
        return User.builder()
                .name(name)
                .email(email)
                .emailVerifiedAt(null)
                .password(password)
                .address(address)
                .phoneNumber(phoneNumber)
                .role(role)
                .deletedAt(null)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return CustomAuthorityUtils.createAuthorities(role);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
