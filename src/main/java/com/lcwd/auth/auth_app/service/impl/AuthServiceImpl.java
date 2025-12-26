package com.lcwd.auth.auth_app.service.impl;

import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.service.AuthService;
import com.lcwd.auth.auth_app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDtos registerUser(UserDtos userDtos) {
        String encodedPassword = passwordEncoder.encode(userDtos.getPassword());
        userDtos.setPassword(encodedPassword);
        UserDtos user = userService.createUser(userDtos);
        return user;
    }
}
