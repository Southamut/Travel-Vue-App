package com.techup.spring_demo.controller;

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
}