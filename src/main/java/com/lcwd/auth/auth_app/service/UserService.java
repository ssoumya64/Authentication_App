package com.lcwd.auth.auth_app.service;

import com.lcwd.auth.auth_app.dtos.UserDtos;

import java.util.UUID;

public interface UserService {
    UserDtos createUser(UserDtos userDtos);
    UserDtos getUserByEmail(String email);
    UserDtos updateUser(UserDtos userDtos, UUID userId);
    Iterable<UserDtos>  getAllUsers();
    void deleteUser(UUID userId);
}