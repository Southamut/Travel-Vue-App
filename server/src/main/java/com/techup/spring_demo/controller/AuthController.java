package com.techup.spring_demo.controller;

import com.techup.spring_demo.dto.LoginRequest;
import com.techup.spring_demo.dto.LoginResponse;
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
                    request.getPassword()
            );
            
            if (result.isSuccess()) {
                RegisterResponse response = new RegisterResponse(
                        result.getMessage(),
                        result.getUserId(),
                        result.getEmail()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                RegisterResponse response = new RegisterResponse(
                        result.getMessage(),
                        null,
                        result.getEmail()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (RuntimeException e) {
            RegisterResponse response = new RegisterResponse(
                    e.getMessage(),
                    null,
                    request.getEmail()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            SupabaseAuthService.LoginResult result = supabaseAuthService.login(
                    request.getEmail(),
                    request.getPassword()
            );
            
            if (result.isSuccess()) {
                LoginResponse response = new LoginResponse(
                        result.getAccessToken(),
                        result.getRefreshToken(),
                        new LoginResponse.UserInfo(result.getUserId(), result.getEmail())
                );
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse(
                        null,
                        null,
                        new LoginResponse.UserInfo(null, result.getEmail())
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            null,
                            null,
                            new LoginResponse.UserInfo(null, request.getEmail())
                    ));
        }
    }
}