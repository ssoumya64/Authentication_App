package com.lcwd.auth.auth_app.controller;

import com.lcwd.auth.auth_app.Repository.UserRepository;
import com.lcwd.auth.auth_app.dtos.LoginRequest;
import com.lcwd.auth.auth_app.dtos.TokenResponse;
import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.entity.Users;
import com.lcwd.auth.auth_app.security.JwtService;
import com.lcwd.auth.auth_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest){
        Authentication authenticate = authenticate(loginRequest);
        Users users = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid Username and Password"));
        if(!users.isEnable()){
            throw new DisabledException("User is disabled");
        }
        String accessToken = jwtService.generateAccessToken(users);
        TokenResponse tokenResponse = TokenResponse.of(accessToken, "", jwtService.getAccessTtlSeconds(), modelMapper.map(users, UserDtos.class));
        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try{
          return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        }catch (Exception e){
            throw new BadCredentialsException("Username or password not valid");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtos> registerUser(@RequestBody UserDtos userDtos){
        UserDtos userDtos1 = authService.registerUser(userDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtos1);
    }
}
