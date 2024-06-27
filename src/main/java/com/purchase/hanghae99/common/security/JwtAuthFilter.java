package com.purchase.hanghae99.common.security;

import com.purchase.hanghae99.common.CustomCookieManager;
import com.purchase.hanghae99.common.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.purchase.hanghae99.common.CustomCookieManager.*;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomCookieManager cookieManager;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = cookieManager.getCookie(request, ACCESS_TOKEN);
            String refreshToken = cookieManager.getCookie(request, REFRESH_TOKEN);

            if (StringUtils.hasText(accessToken) && getLogoutInfo(refreshToken)) {
                if (!jwtProvider.isTokenValid(accessToken)) {
                    jwtProvider.reissue(request, response);
                }

                setAuthentication(accessToken);
            }
        } catch (Exception e) {
            request.setAttribute("message", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean getLogoutInfo(String refreshToken) {
        return redisService.getValues(refreshToken).equals("false");
    }

    private void setAuthentication(String accessToken) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

//    TODO: 나중에 로직에 따라 필터를 거치는 로직인지 아닌지 구분해야함
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return super.shouldNotFilter(request);
//    }
}
