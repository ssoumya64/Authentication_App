package com.lcwd.auth.auth_app.security;

import com.lcwd.auth.auth_app.Repository.UserRepository;
import com.lcwd.auth.auth_app.entity.Provider;
import com.lcwd.auth.auth_app.entity.RefreshToken;
import com.lcwd.auth.auth_app.entity.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.lcwd.auth.auth_app.Repository.*;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final Logger logger= LoggerFactory.getLogger(this.getClass());
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final CookiesService cookiesService;
  private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Sucessful Authentication");
        logger.info(authentication.toString());

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        //identify User
        String registrationId="unknown";
        if(authentication instanceof OAuth2AuthenticationToken token){
            registrationId= token.getAuthorizedClientRegistrationId();
        }

        logger.info(" registrationId: "+registrationId);
        logger.info(" user "+oAuth2User.getAttributes().toString());


        Users users;

        switch (registrationId) {

            case "google" -> {

                String email = oAuth2User.getAttributes()
                        .getOrDefault("email", "")
                        .toString();

                String name = oAuth2User.getAttributes()
                        .getOrDefault("name", "")
                        .toString();

                users = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            logger.info("Creating new GOOGLE user with email {}", email);
                            return userRepository.save(
                                    Users.builder()
                                            .email(email)
                                            .username(name)
                                            .provider(Provider.GOOGLE)
                                            .enable(true)
                                            .build()
                            );
                        });
            }
            default -> throw new RuntimeException("Unsupported OAuth provider: " + registrationId);
        }
        String jti = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder().jti(jti).users(users)
                .revoked(false)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds())).build();
        refreshTokenRepository.save(refreshToken);
        String accessToken = jwtService.generateAccessToken(users);
        String generateRefreshToken = jwtService.generateRefreshToken(users, refreshToken.getJti());
        
        cookiesService.attachRefreshCookie(request,response, generateRefreshToken, (int)jwtService.getRefreshTtlSeconds());

        response.getWriter().write("Login Successful");
    }
}
