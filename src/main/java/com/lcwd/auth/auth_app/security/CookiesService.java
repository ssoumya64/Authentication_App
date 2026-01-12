package com.lcwd.auth.auth_app.security;

import jakarta.servlet.http.HttpServletRequest;
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
    private final String cookieSameSite;

    public CookiesService(
            @Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
            @Value("${security.jwt.cookie-domain}") String cookieDomain,
            @Value("${security.jwt.cookie-same-site}") String cookieSameSite) {

        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
    }

    // Attach refresh token cookie
    public void attachRefreshCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String value,
                                    int maxAge) {

        boolean isLocal = isLocalRequest(request);

        boolean secure = isLocal ? false : cookieSecure;
        String sameSite = isLocal ? "Lax" : cookieSameSite;

        var responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite);

        if (cookieDomain != null && !cookieDomain.isBlank() && !isLocal) {
            responseCookieBuilder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    // Clear refresh cookie
    public void clearRefreshCookie(HttpServletRequest request, HttpServletResponse response) {

        boolean isLocal = isLocalRequest(request);
        boolean secure = isLocal ? false : cookieSecure;
        String sameSite = isLocal ? "Lax" : cookieSameSite;

        var builder = ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(cookieHttpOnly)
                .path("/")
                .sameSite(sameSite)
                .secure(secure);

        if (cookieDomain != null && !cookieDomain.isBlank() && !isLocal) {
            builder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public void addNoStoreHeader(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("pragma", "no-cache");
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String host = request.getServerName();
        return host.equals("localhost") || host.equals("127.0.0.1");
    }
}
