package com.mps.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mps.common.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private record Window(Instant startedAt, AtomicInteger count) {}
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Clock clock;
    private final int maximum;
    private final long windowSeconds;
    public LoginRateLimitFilter(ObjectMapper mapper,
            @Value("${mps.security.login-rate-limit.max-attempts:20}") int maximum,
            @Value("${mps.security.login-rate-limit.window-seconds:60}") long windowSeconds) {
        this(mapper, Clock.systemUTC(), maximum, windowSeconds);
    }
    LoginRateLimitFilter(ObjectMapper mapper, Clock clock, int maximum, long windowSeconds) {
        this.mapper=mapper; this.clock=clock; this.maximum=maximum; this.windowSeconds=windowSeconds;
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/api/v1/auth/login".equals(request.getRequestURI()));
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Instant now=clock.instant(); String key=request.getRemoteAddr();
        Window window=windows.compute(key,(k,old)->old==null||old.startedAt().plusSeconds(windowSeconds).isBefore(now)
                ?new Window(now,new AtomicInteger()):old);
        int attempt=window.count().incrementAndGet();
        response.setHeader("X-RateLimit-Limit", String.valueOf(maximum));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0,maximum-attempt)));
        if(attempt>maximum){response.setStatus(429);response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After",String.valueOf(windowSeconds));mapper.writeValue(response.getOutputStream(),ApiResponse.error("Too many login attempts. Try again later."));return;}
        chain.doFilter(request,response);
    }
}
