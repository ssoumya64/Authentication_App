package com.lcwd.auth.auth_app.exceptions;

import com.lcwd.auth.auth_app.dtos.ApiError;
import com.lcwd.auth.auth_app.dtos.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.CredentialException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
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
 @ExceptionHandler({UsernameNotFoundException.class,
                    BadCredentialsException.class,
                    CredentialException.class,
                    AuthenticationException.class,
                    DisabledException.class
                  })
 public ResponseEntity<ApiError> handleAuthException(Exception e, HttpServletRequest request){
     logger.info("Exception class", e.getClass().getName());
     ApiError apiError = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage(), request.getRequestURI());
     return ResponseEntity.badRequest().body(apiError);
 }
}
