package com.lcwd.auth.auth_app.service.impl;

import com.lcwd.auth.auth_app.Repository.UserRepository;
import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDtos createUser(UserDtos userDtos) {
        return null;
    }

    @Override
    public UserDtos getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserDtos updateUser(UserDtos userDtos, String userId) {
        return null;
    }

    @Override
    public Iterable<UserDtos> getAllUsers() {
        return null;
    }
}
