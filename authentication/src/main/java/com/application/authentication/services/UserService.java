package com.application.authentication.services;

import com.application.authentication.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    UserDto getMe(Authentication authentication);
    UserDto getUserById(int id);
}
