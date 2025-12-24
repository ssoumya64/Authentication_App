package com.lcwd.auth.auth_app.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
    public ResourceNotFoundException(String message, Throwable causes){
        super("Resource Not Found");
    }
}
