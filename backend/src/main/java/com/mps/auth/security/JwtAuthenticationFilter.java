package com.mps.auth.security;

import com.mps.auth.repository.AppUserRepository;
import com.mps.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final AppUserRepository users;
    public JwtAuthenticationFilter(JwtService jwt, AppUserRepository users) { this.jwt = jwt; this.users = users; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String token = header.substring(7);
                var user = users.findById(jwt.userId(token)).orElse(null);
                if (user != null) {
                    user.unlockIfExpired();
                    var principal = UserPrincipal.from(user);
                    if (principal.enabled()) SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, token, principal.authorities()));
                }
            } catch (JwtException | IllegalArgumentException ignored) { }
        }
        chain.doFilter(request, response);
    }
}
