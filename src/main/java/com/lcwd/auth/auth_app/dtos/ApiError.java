package com.lcwd.auth.auth_app.dtos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timeStamp
) {
    public static ApiError of(int status, String error,String message, String path){
        return new ApiError(status,error,message,path,OffsetDateTime.now(ZoneOffset.UTC));
    }

    public static ApiError of(int status, String error,String message, String path, boolean DateTime){
        return new ApiError(status,error,message,path,null);
    }
}
