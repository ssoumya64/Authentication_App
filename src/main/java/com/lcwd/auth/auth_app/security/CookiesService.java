package com.lcwd.auth.auth_app.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CookiesService {
    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String cookiwSameSite;

    public CookiesService(@Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
                          @Value("${security.jwt.cookie-http-only}")boolean cookieHttpOnly,
                          @Value("${security.jwt.cookie-secure}")boolean cookieSecure,
                          @Value("${security.jwt.cookie-domain}") String cookieDomain,
                          @Value("${security.jwt.cookie-same-site}")String cookiwSameSite) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.cookiwSameSite = cookiwSameSite;
    }
    //create method to attach cookie in response
    public void attachRefreshCookie(HttpServletResponse response, String value, int maxAge){
        var responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookiwSameSite);
        if(cookieDomain!=null && !cookieDomain.isBlank()){
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responsecookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responsecookie.toString());
    }

    //clear refresh cookie
    public void clearRefreshCookie(HttpServletResponse response){
        var builder = ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(cookieHttpOnly)
                .path("/")
                .sameSite(cookiwSameSite)
                .secure(cookieSecure);

        if(cookieDomain!=null && !cookieDomain.isBlank()){
            builder.domain(cookieDomain);
        }
        ResponseCookie responsecookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responsecookie.toString());
    }

    public void addNoStoreHeader(HttpServletResponse response){
        response.setHeader(HttpHeaders.CACHE_CONTROL,"no-store");
        response.setHeader("pragma","no-cache");
    }

}
