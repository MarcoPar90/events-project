package com.application.authentication.services.impl;

import com.application.authentication.dto.UserDto;
import com.application.authentication.entity.User;
import com.application.authentication.exception.NotFoundException;
import com.application.authentication.repository.UserRepository;
import com.application.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public UserDto getMe(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return this.convertUSerEntityToDto(user);
    }

    @Override
    public UserDto getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", id)));
        return convertUSerEntityToDto(user);
    }

    public UserDto convertUSerEntityToDto(UserDetails user) {
        return modelMapper.map(user, UserDto.class);
    }
}
