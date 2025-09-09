package com.application.booking.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtilities {
    @Value("${jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {
        String tokenWithoutBearer = token.substring(7);
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(tokenWithoutBearer)
                .getBody();
    }
}
