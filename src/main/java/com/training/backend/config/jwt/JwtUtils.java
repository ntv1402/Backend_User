package com.training.backend.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.training.backend.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String generateToken(AuthUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + Constants.JWT_EXPIRATION * 1000);

        return JWT.create()
                .withIssuer("self")
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withSubject(userDetails.getUser().getUsername())
                .withClaim("user", toMap(userDetails.getUser()))
                .sign(Algorithm.HMAC512(Constants.JWT_SECRET));
    }

    private Map<String, Object> toMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (Arrays.stream(Constants.ATTRIBUTIES_TO_TOKEN).anyMatch(field.getName()::equals)) {
                try {
                    map.put(field.getName(), field.get(object));
                } catch (IllegalAccessException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        return map;
    }

    public String getUsernameFromJWT(String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            DecodedJWT token = JWT.require(Algorithm.HMAC512(Constants.JWT_SECRET)).build().verify(authToken);

            // check token Expire
            Date expireAt = token.getExpiresAt();
            if (expireAt.compareTo(new Date()) > 0) {
                return true;
            }
        } catch (JWTVerificationException ex) {
            logger.error("Invalid JWT token");
        }
        return false;
    }

}
