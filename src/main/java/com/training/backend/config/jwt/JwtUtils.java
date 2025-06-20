package com.training.backend.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    /**
     * Tạo JWT từ username
     */
    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(getAlgorithm());
    }

    /**
     * Trích xuất username từ token
     */
    public String getUsernameFromToken(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * Kiểm tra token có hợp lệ hay không
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(getAlgorithm()).build().verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }
}
