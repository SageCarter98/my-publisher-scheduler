package com.mps.auth.service;

import com.mps.auth.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessMinutes;
    private final String issuer;
    public JwtService(@Value("${mps.security.jwt.secret}") String secret,
                      @Value("${mps.security.jwt.access-minutes:15}") long accessMinutes,
                      @Value("${mps.security.jwt.issuer:mps}") String issuer) {
        byte[] decoded;
        try { decoded = Decoders.BASE64.decode(secret); } catch (Exception ignored) { decoded = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8); }
        if (decoded.length < 32) throw new IllegalArgumentException("MPS JWT secret must contain at least 32 bytes.");
        this.key = Keys.hmacShaKeyFor(decoded); this.accessMinutes = accessMinutes; this.issuer = issuer;
    }
    public String createAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        List<String> authorities = principal.authorities().stream().map(Object::toString).toList();
        return Jwts.builder().issuer(issuer).subject(principal.userId().toString()).issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessMinutes * 60))).claim("org", principal.organizationId().toString())
                .claim("email", principal.email()).claim("authorities", authorities).signWith(key).compact();
    }
    public Claims parse(String token) { return Jwts.parser().verifyWith(key).requireIssuer(issuer).build().parseSignedClaims(token).getPayload(); }
    public UUID userId(String token) { return UUID.fromString(parse(token).getSubject()); }
    public long accessExpiresInSeconds() { return accessMinutes * 60; }
}
