package com.purchase.hanghae99.common.security;

import com.purchase.hanghae99.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.file.AccessDeniedException;
import java.util.List;

public class CustomAuthorityUtils {
    public static List<GrantedAuthority> createAuthorities(UserRole role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }

    public static void verifiedRole(UserRole role) throws Exception {
        if (role.toString() == null || (!role.toString().equals(UserRole.CERTIFIED_USER.toString()) && !role.toString().equals(UserRole.ADMIN.toString()))) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}
