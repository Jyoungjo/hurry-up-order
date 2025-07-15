package com.common.core.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtParser {
    @Getter
    private static Key key;

    @Value("${jwt.secretKey}")
    public void setKey(String secretKey) {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        JwtParser.key = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    public static String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody().get("email", String.class);
    }
}