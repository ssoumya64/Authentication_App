package com.lcwd.auth.auth_app.dtos;

public record TokenResponse (
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        UserDtos userDtos
    ){

    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn
                                   , UserDtos userDtos){
        return new TokenResponse(accessToken, refreshToken, expiresIn, "Bearer", userDtos);
    }
}
