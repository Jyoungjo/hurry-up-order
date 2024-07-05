package com.purchase.preorder.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomCookieManager {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    public static void setCookie(HttpServletResponse res, String value, String name, long expiration) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(expiration)
                .path("/")
//                .secure(true)
                .httpOnly(true)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    public static String getCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setCookie(ServerHttpResponse res, String value, String name, long expiration) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(expiration)
                .path("/")
//                .secure(true)
                .httpOnly(true)
                .build();
        res.addCookie(cookie);
    }

    public static String getCookie(ServerHttpRequest req, String name) {
        return Objects.requireNonNull(req.getCookies().getFirst(name)).getValue();
    }

    public static void deleteCookie(HttpServletResponse res, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .maxAge(0)
                .path("/")
//                .secure(true)
                .httpOnly(true)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }
}
