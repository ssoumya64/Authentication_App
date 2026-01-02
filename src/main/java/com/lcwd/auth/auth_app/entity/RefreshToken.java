package com.lcwd.auth.auth_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token", indexes={
        @Index(name = "refresh_token_jti_idx", columnList = "jti",unique = true),
        @Index(name = "refresh_token_user_id_idx", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "jti", unique = true, nullable = false, updatable = false)
    private String jti;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users users;
    @Column(updatable = false, nullable = false)
    private Instant createdAt=Instant.now();
    @Column(nullable = false)
    private Instant expireAt;
    @Column(nullable = false)
    private boolean revoked;
    private String replaceByToken;
}
