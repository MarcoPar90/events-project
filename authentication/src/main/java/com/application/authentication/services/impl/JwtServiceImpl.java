package com.application.authentication.services.impl;

import com.application.authentication.services.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    @Override
    public String extractUserName(String token, String secret) {
        return extractClaim(token, Claims::getSubject, secret);
    }

    @Override
    public String generateToken(UserDetails userDetails, String secret, long expiration) {
        return generateToken(new HashMap<>(), userDetails, secret, expiration);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails, String secret) {
        final String userName = extractUserName(token, secret);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token, secret);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers, String secret) {
        final Claims claims = extractAllClaims(token, secret);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String secret, long expiration) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(secret), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token, String secret) {
        return extractExpiration(token, secret).before(new Date());
    }

    private Date extractExpiration(String token, String secret) {
        return extractClaim(token, Claims::getExpiration, secret);
    }

    private Claims extractAllClaims(String token, String secret) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey(secret)).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
