package com.common.web.auth;

import com.common.core.util.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthUtils {

    public static String getUserEmail(HttpServletRequest request, JwtUtils jwtUtils) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));
        return JwtParser.getEmail(accessToken);
    }
}
