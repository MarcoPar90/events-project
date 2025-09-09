package com.application.authentication.services.impl;

import com.application.authentication.dao.request.SignUpRequest;
import com.application.authentication.dao.request.SigninRequest;
import com.application.authentication.dao.response.JwtAuthenticationRefreshResponse;
import com.application.authentication.dao.response.JwtAuthenticationResponse;
import com.application.authentication.entity.User;
import com.application.authentication.enumeration.Role;
import com.application.authentication.repository.UserRepository;
import com.application.authentication.services.AuthenticationService;
import com.application.authentication.services.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Value("${token.expiration.milliseconds}")
    private long jwtExpiration;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.expiration.refresh-milliseconds}")
    private long jwtRefreshExpiration;

    @Value("${token.signing.refresh-key}")
    private String jwtSigningRefreshKey;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        User user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        String jwt = jwtService.generateToken(user, jwtSigningKey, jwtExpiration);
        String refreshJwt = jwtService.generateToken(user, jwtSigningRefreshKey, jwtRefreshExpiration);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .expiration(jwtExpiration)
                .refreshToken(refreshJwt)
                .refreshExpiration(jwtRefreshExpiration)
                .build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        String jwt = jwtService.generateToken(user, jwtSigningKey, jwtExpiration);
        String refreshJwt = jwtService.generateToken(user, jwtSigningRefreshKey, jwtRefreshExpiration);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .expiration(jwtExpiration)
                .refreshToken(refreshJwt)
                .refreshExpiration(jwtRefreshExpiration)
                .build();
    }

    @Override
    public JwtAuthenticationRefreshResponse refresh(JwtAuthenticationResponse jwt) {
        String username = jwtService.extractUserName(jwt.getRefreshToken(), jwtSigningRefreshKey);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt.getRefreshToken(), userDetails, jwtSigningRefreshKey)) {
            String newAccessToken = jwtService.generateToken(userDetails, jwtSigningKey, jwtExpiration);

            return JwtAuthenticationRefreshResponse.builder()
                    .token(newAccessToken)
                    .expiration(jwtExpiration)
                    .build();
        } else {
            throw new JwtException("Refresh token non valido");
        }
    }

}
