package com.purchase.hanghae99_order.common.security;

import com.purchase.hanghae99_order.common.CustomCookieManager;
import com.purchase.hanghae99_order.common.RedisService;
import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_core.exception.ExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomCookieManager cookieManager;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = cookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
            String refreshToken = cookieManager.getCookie(request, CustomCookieManager.REFRESH_TOKEN);

            if (checkAccessToken(accessToken)) {
                setAuthentication(accessToken);
            } else if (checkRefreshToken(refreshToken)) {
                accessToken = jwtProvider.reissue(request, response);
                setAuthentication(accessToken);
            } else {
                throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } catch (Exception e) {
            request.setAttribute("message", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkAccessToken(String accessToken) {
        return StringUtils.hasText(accessToken) && isTokenValid(accessToken);
    }

    private boolean checkRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) && isTokenValid(refreshToken) && getLogoutInfo(refreshToken);
    }

    private boolean getLogoutInfo(String refreshToken) {
        return redisService.getValues(refreshToken).equals("false");
    }

    private boolean isTokenValid(String token) {
        try {
            return jwtProvider.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    TODO: 나중에 로직에 따라 필터를 거치는 로직인지 아닌지 구분해야함
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return super.shouldNotFilter(request);
//    }
}
