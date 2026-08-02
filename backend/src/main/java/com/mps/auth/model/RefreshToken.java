package com.mps.auth.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "created_ip", length = 64)
    private String createdIp;
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    protected RefreshToken() {}
    public RefreshToken(AppUser user, String tokenHash, Instant expiresAt, String createdIp, String userAgent) {
        this.user = user; this.tokenHash = tokenHash; this.expiresAt = expiresAt; this.createdIp = createdIp; this.userAgent = userAgent;
    }
    public AppUser getUser() { return user; }
    public boolean isUsable() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
    public void revoke() { revokedAt = Instant.now(); }
}
