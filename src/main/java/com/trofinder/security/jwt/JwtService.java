package com.trofinder.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Sinh Access Token
     */
    public String generateAccessToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Sinh Refresh Token
     */
    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + jwtProperties.getRefreshExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Lấy username từ token
     */
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();

    }

    /**
     * Token còn hạn không
     */
    public boolean isTokenValid(String token, String username) {

        return extractUsername(token).equals(username)
                && !isTokenExpired(token);

    }

    /**
     * Kiểm tra hết hạn
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }

    /**
     * Lấy ngày hết hạn
     */
    private Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();

    }

    /**
     * Đọc Claims
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * Secret Key
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        } catch (Exception e) {
            keyBytes = jwtProperties.getSecret()
                    .getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);

    }

}