package com.techup.spring_demo.controller;

import com.techup.spring_demo.dto.LoginRequest;
import com.techup.spring_demo.dto.LoginResponse;
import com.techup.spring_demo.dto.LogoutResponse;
import com.techup.spring_demo.dto.UserResponse;
import com.techup.spring_demo.dto.RegisterRequest;
import com.techup.spring_demo.dto.RegisterResponse;
import com.techup.spring_demo.service.SupabaseAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final SupabaseAuthService supabaseAuthService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            SupabaseAuthService.RegisterResult result = supabaseAuthService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getDisplayName(),
                    request.getAvatarUrl());

            if (result.isSuccess()) {
                RegisterResponse response = new RegisterResponse(
                        result.getMessage(),
                        result.getUserId(),
                        result.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                RegisterResponse response = new RegisterResponse(
                        result.getMessage(),
                        null,
                        result.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (RuntimeException e) {
            RegisterResponse response = new RegisterResponse(
                    e.getMessage(),
                    null,
                    request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            SupabaseAuthService.LoginResult result = supabaseAuthService.login(
                    request.getEmail(),
                    request.getPassword());

            if (result.isSuccess()) {
                LoginResponse response = new LoginResponse(
                        result.getAccessToken(),
                        result.getRefreshToken(),
                        new LoginResponse.UserInfo(result.getUserId(), result.getEmail()));
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse(
                        null,
                        null,
                        new LoginResponse.UserInfo(null, result.getEmail()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (RuntimeException e) {
            // Let GlobalExceptionHandler handle it to return error message
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LogoutResponse("Authorization token required"));
        }

        try {
            String token = authorization.substring(7); // Remove "Bearer " prefix
            SupabaseAuthService.LogoutResult result = supabaseAuthService.logout(token);

            if (result.isSuccess()) {
                return ResponseEntity.ok(new LogoutResponse(result.getMessage()));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new LogoutResponse(result.getMessage()));
            }

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new LogoutResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized: No token provided");
        }

        String token = authorization.substring(7);
        SupabaseAuthService.UserResult result = supabaseAuthService.getCurrentUser(token);

        if (result.isSuccess()) {
            UserResponse response = new UserResponse(
                    result.getId(),
                    result.getEmail(),
                    result.getDisplayName(),
                    result.getCreatedAt());
            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("Failed to get user information");
        }
    }

}