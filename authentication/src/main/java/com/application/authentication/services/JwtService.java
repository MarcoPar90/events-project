package com.application.authentication.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token, String secret);

    String generateToken(UserDetails userDetails, String token, long expiration);

    boolean isTokenValid(String token, UserDetails userDetails, String secret);
}
