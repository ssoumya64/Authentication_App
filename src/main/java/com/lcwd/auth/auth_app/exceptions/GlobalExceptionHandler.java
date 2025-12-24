package com.lcwd.auth.auth_app.exceptions;

import com.lcwd.auth.auth_app.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(ResourceNotFoundException.class)
 public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception){
     ErrorResponse serverError = new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, "Internal Server Error");
     return ResponseEntity.status(404).body(serverError);
 }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception){
        ErrorResponse serverError = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, "Internal Server Error");
        return ResponseEntity.status(404).body(serverError);
    }
}
