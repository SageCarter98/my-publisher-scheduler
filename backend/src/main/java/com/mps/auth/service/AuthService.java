package com.mps.auth.service;

import com.mps.auth.dto.AuthResponse;
import com.mps.auth.dto.LoginRequest;
import com.mps.auth.model.AppUser;
import com.mps.auth.model.RefreshToken;
import com.mps.auth.model.UserStatus;
import com.mps.auth.repository.AppUserRepository;
import com.mps.auth.repository.RefreshTokenRepository;
import com.mps.auth.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {
    private final AppUserRepository users; private final RefreshTokenRepository refreshTokens; private final PasswordEncoder passwords;
    private final JwtService jwt; private final TokenHashService hashes; private final SecureRandom random = new SecureRandom();
    private final int maxAttempts; private final long lockMinutes; private final long refreshDays;
    public AuthService(AppUserRepository users, RefreshTokenRepository refreshTokens, PasswordEncoder passwords, JwtService jwt,
                       TokenHashService hashes, @Value("${mps.security.lockout.max-attempts:5}") int maxAttempts,
                       @Value("${mps.security.lockout.minutes:15}") long lockMinutes,
                       @Value("${mps.security.jwt.refresh-days:14}") long refreshDays) {
        this.users=users; this.refreshTokens=refreshTokens; this.passwords=passwords; this.jwt=jwt; this.hashes=hashes;
        this.maxAttempts=maxAttempts; this.lockMinutes=lockMinutes; this.refreshDays=refreshDays;
    }
    @Transactional public AuthResponse login(LoginRequest request, String ip, String userAgent) {
        AppUser user = users.findFirstByEmailIgnoreCase(request.email()).orElseThrow(() -> new BadCredentialsException("Invalid email or password."));
        user.unlockIfExpired();
        if (user.getStatus() != UserStatus.ACTIVE || !passwords.matches(request.password(), user.getPasswordHash())) {
            if (user.getStatus() == UserStatus.ACTIVE) user.recordFailedLogin(maxAttempts, lockMinutes);
            throw new BadCredentialsException("Invalid email or password.");
        }
        user.recordSuccessfulLogin();
        return issue(user, ip, userAgent);
    }
    @Transactional public AuthResponse refresh(String rawToken, String ip, String userAgent) {
        var stored = refreshTokens.findByTokenHash(hashes.hash(rawToken)).orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));
        if (!stored.isUsable() || stored.getUser().getStatus() != UserStatus.ACTIVE) throw new BadCredentialsException("Invalid refresh token.");
        stored.revoke();
        return issue(stored.getUser(), ip, userAgent);
    }
    @Transactional public void logout(String rawToken) { refreshTokens.findByTokenHash(hashes.hash(rawToken)).ifPresent(RefreshToken::revoke); }
    @Transactional public void logoutAll(java.util.UUID userId) { refreshTokens.revokeAllForUser(userId, Instant.now()); }
    public AuthResponse.UserView view(AppUser user) {
        return new AuthResponse.UserView(user.getId(), user.getOrganization().getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getRoles().stream().map(r -> r.getCode()).sorted().toList());
    }
    private AuthResponse issue(AppUser user, String ip, String userAgent) {
        UserPrincipal principal = UserPrincipal.from(user);
        String access = jwt.createAccessToken(principal);
        byte[] bytes = new byte[48]; random.nextBytes(bytes); String rawRefresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshToken(user, hashes.hash(rawRefresh), Instant.now().plusSeconds(refreshDays * 86400), ip, truncate(userAgent, 500)));
        return new AuthResponse(access, rawRefresh, jwt.accessExpiresInSeconds(), view(user));
    }
    private static String truncate(String value, int max) { return value == null ? null : value.substring(0, Math.min(value.length(), max)); }
}
