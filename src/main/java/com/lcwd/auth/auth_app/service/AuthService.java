package com.lcwd.auth.auth_app.service;

import com.lcwd.auth.auth_app.dtos.UserDtos;

public interface AuthService {
    UserDtos registerUser(UserDtos userDtos);
}
