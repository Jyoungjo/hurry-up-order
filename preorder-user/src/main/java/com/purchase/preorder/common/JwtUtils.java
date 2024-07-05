package com.purchase.preorder.common;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.user.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class JwtUtils {
    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "email"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    public static final String EMAIL_CLAIM = "email";
    private Key cachedSecretKey;

    @Value("${jwt.secretKey}")
    private String secretKeyPlain;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

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
}
