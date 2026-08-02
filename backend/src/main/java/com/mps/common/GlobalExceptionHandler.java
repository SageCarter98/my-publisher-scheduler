package com.mps.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.unprocessableEntity().body(Map.of("success", false, "message", "Validation failed.", "errors", errors));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Void>> constraint(ConstraintViolationException ex) { return ResponseEntity.unprocessableEntity().body(ApiResponse.error("Validation failed.")); }
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiResponse<Void>> credentials(BadCredentialsException ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid email or password.")); }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiResponse<Void>> status(ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(ex.getReason() == null ? "Request failed." : ex.getReason())); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiResponse<Void>> illegal(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage())); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred.")); }
}
