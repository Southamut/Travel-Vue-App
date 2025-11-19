package com.techup.spring_demo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techup.spring_demo.config.SupabaseConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseAuthService {
    
    private final WebClient supabaseWebClient;
    private final SupabaseConfig supabaseConfig;
    
    public RegisterResult register(String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(password);
        
        try {
            RegisterResponse response = supabaseWebClient
                    .post()
                    .uri("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(RegisterResponse.class)
                    .block();
            
            if (response != null && response.getUser() != null) {
                return new RegisterResult(true, "User registered successfully", response.getUser().getId(), email);
            }
            return new RegisterResult(false, "Registration failed", null, email);
            
        } catch (WebClientResponseException e) {
            // Parse error response from Supabase
            String errorMessage = "Registration failed";
            if (e.getResponseBodyAsString() != null && e.getResponseBodyAsString().contains("already registered")) {
                errorMessage = "Email is already registered";
            } else if (e.getResponseBodyAsString() != null && e.getResponseBodyAsString().contains("Password")) {
                errorMessage = "Password does not meet requirements";
            } else if (e.getStatusCode().is4xxClientError()) {
                errorMessage = "Invalid email or password";
            }
            
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
    
    @Data
    private static class RegisterRequest {
        private String email;
        private String password;
    }
    
    @Data
    private static class RegisterResponse {
        private User user;
        private String access_token;
        private String refresh_token;
        
        @Data
        private static class User {
            private String id;
            private String email;
            @JsonProperty("created_at")
            private String createdAt;
        }
    }
    
    @Data
    public static class RegisterResult {
        private boolean success;
        private String message;
        private String userId;
        private String email;
        
        public RegisterResult(boolean success, String message, String userId, String email) {
            this.success = success;
            this.message = message;
            this.userId = userId;
            this.email = email;
        }
    }
}