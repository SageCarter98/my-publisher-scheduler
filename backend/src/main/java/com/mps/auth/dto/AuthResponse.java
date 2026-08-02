package com.mps.auth.dto;
import java.util.List;
import java.util.UUID;
public record AuthResponse(String accessToken, String refreshToken, long expiresInSeconds, UserView user) {
    public record UserView(UUID id, UUID organizationId, String email, String firstName, String lastName, List<String> roles) {}
}
