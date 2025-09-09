package com.application.authentication.services;

import com.application.authentication.dao.request.SignUpRequest;
import com.application.authentication.dao.request.SigninRequest;
import com.application.authentication.dao.response.JwtAuthenticationRefreshResponse;
import com.application.authentication.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);

    JwtAuthenticationRefreshResponse refresh(JwtAuthenticationResponse request);
}
