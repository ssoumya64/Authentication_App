package com.lcwd.auth.auth_app.service;

import com.lcwd.auth.auth_app.dtos.UserDtos;

public interface UserService {
    UserDtos createUser(UserDtos userDtos);
    UserDtos getUserByEmail(String email);
    UserDtos updateUser(UserDtos userDtos, String userId);
    Iterable<UserDtos>  getAllUsers();
}