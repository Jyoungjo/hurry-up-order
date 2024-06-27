package com.purchase.hanghae99.common.security;

import com.purchase.hanghae99.common.RedisService;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.common.CustomCookieManager;
import com.purchase.hanghae99.user.UserRepository;
import com.purchase.hanghae99.user.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

import static com.purchase.hanghae99.common.CustomCookieManager.ACCESS_TOKEN;
import static com.purchase.hanghae99.common.CustomCookieManager.REFRESH_TOKEN;
import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static io.jsonwebtoken.SignatureAlgorithm.*;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class JwtProvider {
    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "email"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    public static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private final UserRepository userRepository;
    private Key cachedSecretKey;
    private final CustomCookieManager cookieManager;
    private final RedisService redisService;

    @Value("${jwt.secretKey}")
    private String secretKeyPlain;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private Key _getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    public Key getSecretKey() {
        if (cachedSecretKey == null) cachedSecretKey = _getSecretKey();
        return cachedSecretKey;
    }

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(Map<String, Object> claims) {
        Date now = new Date();
        claims.put("role", UserRole.CERTIFIED_USER);

        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(getSecretKey(), HS512)
                .compact();
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(getSecretKey(), HS512)
                .compact();
    }

    /**
     * AccessToken에서 Email 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후
     * 유효하다면 getClaim()으로 이메일 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaims(accessToken);
        CustomUserDetails customUserDetails = CustomUserDetails.of(claims.get("email", String.class), UserRole.valueOf(claims.get("role", String.class)));
        return new UsernamePasswordAuthenticationToken(customUserDetails.getEmail(), null, customUserDetails.getAuthorities());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(UNSUPPORTED_JWT);
        } catch (MalformedJwtException e) {
            throw new BusinessException(INVALID_JWT);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ILLEGAL_ARGUMENT_JWT);
        }
        return true;
    }

    public String reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieManager.getCookie(request, REFRESH_TOKEN);

        if (!redisService.getValues(refreshToken).equals("false") || !isTokenValid(refreshToken)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }

        Claims claims = getClaims(refreshToken);
        String newAccessToken = createAccessToken(claims);

        cookieManager.setCookie(response, newAccessToken, ACCESS_TOKEN, accessTokenExpirationPeriod);
        return newAccessToken;
    }
}
