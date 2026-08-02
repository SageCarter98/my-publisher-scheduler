package com.mps.auth;

import com.mps.auth.dto.*;
import com.mps.auth.repository.AppUserRepository;
import com.mps.auth.security.UserPrincipal;
import com.mps.auth.service.AuthService;
import com.mps.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth; private final AppUserRepository users;
    public AuthController(AuthService auth, AppUserRepository users) { this.auth=auth; this.users=users; }
    @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servlet) {
        return ApiResponse.ok("Login successful.", auth.login(request, clientIp(servlet), servlet.getHeader("User-Agent")));
    }
    @PostMapping("/refresh") public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request, HttpServletRequest servlet) {
        return ApiResponse.ok("Token refreshed.", auth.refresh(request.refreshToken(), clientIp(servlet), servlet.getHeader("User-Agent")));
    }
    @PostMapping("/logout") public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        auth.logout(request.refreshToken()); return ApiResponse.ok("Logout successful.", null);
    }
    @PostMapping("/logout-all") public ApiResponse<Void> logoutAll(@AuthenticationPrincipal UserPrincipal principal) {
        auth.logoutAll(principal.userId()); return ApiResponse.ok("All sessions revoked.", null);
    }
    @GetMapping("/me") public ApiResponse<AuthResponse.UserView> me(@AuthenticationPrincipal UserPrincipal principal) {
        var user = users.findById(principal.userId()).orElseThrow(); return ApiResponse.ok("Current user.", auth.view(user));
    }
    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For"); return forwarded == null ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
    }
}
