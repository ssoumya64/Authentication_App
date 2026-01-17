package com.lcwd.auth.auth_app.controller;

import com.lcwd.auth.auth_app.Repository.*;
import com.lcwd.auth.auth_app.dtos.LoginRequest;
import com.lcwd.auth.auth_app.dtos.RefreshTokenRequest;
import com.lcwd.auth.auth_app.dtos.TokenResponse;
import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.entity.RefreshToken;
import com.lcwd.auth.auth_app.entity.Users;
import com.lcwd.auth.auth_app.security.CookiesService;
import com.lcwd.auth.auth_app.security.JwtService;
import com.lcwd.auth.auth_app.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final CookiesService cookiesService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest,HttpServletRequest request, HttpServletResponse response){
        Authentication authenticate = authenticate(loginRequest);
        Users users = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid Username and Password"));
        if(!users.isEnable()){
            throw new DisabledException("User is disabled");
        }
        String jti = UUID.randomUUID().toString();
        var refreshToken = RefreshToken.builder().jti(jti).users(users)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        String accessToken = jwtService.generateAccessToken(users);
        String refreshToken1 = jwtService.generateRefreshToken(users, refreshToken.getJti());

        //use cookie service to attach refresh token in cookie
        cookiesService.attachRefreshCookie(request,response,refreshToken1,(int)jwtService.getRefreshTtlSeconds());
        cookiesService.addNoStoreHeader(response);
        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken1, jwtService.getAccessTtlSeconds(), modelMapper.map(users, UserDtos.class));
        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try{
          return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        }catch (Exception e){
            throw new BadCredentialsException("Username or password not valid");
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody(required = false) RefreshTokenRequest refreshTokenRequest
                                                       , HttpServletRequest request,HttpServletResponse response){
        String refreshToken = readRefreshTokenFromRequest(refreshTokenRequest, request).orElseThrow(() -> new BadCredentialsException("Invalid refresh Token"));
        if(!jwtService.isRefreshToken(refreshToken)){
            throw new BadCredentialsException("Invalid Refresh Token");
        }
        String jti = jwtService.getJti(refreshToken);
        UUID userId= jwtService.getUserId(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti).orElseThrow(()-> new BadCredentialsException("Invalid Refresh Token"));
        if(storedRefreshToken.isRevoked()){
            throw new BadCredentialsException("Refresh Token is Revoked or Expired");
        }
        if(storedRefreshToken.getExpireAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh Token Expired");
        }
        if(!storedRefreshToken.getUsers().getId().equals(userId)){
            throw new BadCredentialsException("Refresh token Does not Belong to this user");
        }

        storedRefreshToken.setRevoked(true);
        String newjti = UUID.randomUUID().toString();
        storedRefreshToken.setReplaceByToken(newjti);
        refreshTokenRepository.save(storedRefreshToken);

        Users users1=storedRefreshToken.getUsers();
        RefreshToken newrefreshToken = RefreshToken.builder().jti(newjti).users(users1)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newrefreshToken);
        String newAccessToken = jwtService.generateAccessToken(users1);
        String newRefreshToken = jwtService.generateRefreshToken(users1, newrefreshToken.getJti());
        cookiesService.attachRefreshCookie(request,response,newRefreshToken,(int)jwtService.getRefreshTtlSeconds());
        cookiesService.addNoStoreHeader(response);
        return ResponseEntity.ok(TokenResponse.of(newAccessToken,newRefreshToken, jwtService.getAccessTtlSeconds(),modelMapper.map(users1, UserDtos.class) ));
    }

    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
        if(request.getCookies()!=null){
            Optional<String> fromCookie = Arrays.stream(
                    request.getCookies()
            ).filter(c -> cookiesService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();
            if(fromCookie.isPresent()){
                return fromCookie;
            }
        }
        if(refreshTokenRequest!=null && refreshTokenRequest.refreshToken()!=null && !refreshTokenRequest.refreshToken().isBlank()){
            return Optional.of(refreshTokenRequest.refreshToken());
        }
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if(refreshHeader !=null && !refreshHeader.isBlank()){
            return Optional.of(refreshHeader.trim());
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader!=null && authHeader.regionMatches(true,0,"Bearer ",0,7)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {

                }
            }
        }
        return Optional.empty();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtos> registerUser(@RequestBody UserDtos userDtos){
        UserDtos userDtos1 = authService.registerUser(userDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtos1);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        readRefreshTokenFromRequest(null,request).ifPresent(token->{
            try{
                if(jwtService.isRefreshToken(token)){
                    String jti = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(rt->{
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            }catch (JwtException ignored){

            }
        });
        cookiesService.clearRefreshCookie(request,response);
        cookiesService.addNoStoreHeader(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
