package com.lcwd.auth.auth_app.security;

import com.lcwd.auth.auth_app.entity.Roles;
import com.lcwd.auth.auth_app.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.logging.Level.parse;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer}") String issuer) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    public String generateAccessToken(Users users) {
        Instant now = Instant.now();

        List<String> roles = users.getRoles() == null
                ? List.of()
                : users.getRoles().stream().map(Roles::getName).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(users.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", users.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Users user, String jti) {
        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claims(Map.of("typ", "refresh"))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    //PARSE TOKEN
     public Jws<Claims> parseToken(String token){
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (JwtException e){
            throw e;
        }
    }
    public boolean isAccessToken(String token){
        Claims claims = parseToken(token).getPayload();
        return "access".equals(claims.get("typ"));
    }
    public boolean isRefreshToken(String token){
        Claims body = parseToken(token).getPayload();
        return "refresh".equals(body.get("typ"));
    }
    public UUID getUserId(String token){
        Claims claims = parseToken(token).getPayload();
        return UUID.fromString(claims.getSubject());
    } public String getJti(String token){
        return parseToken(token).getPayload().getId();
    }
}
