package com.techup.spring_demo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techup.spring_demo.config.SupabaseConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseAuthService {

    private final WebClient supabaseWebClient;
    private final SupabaseConfig supabaseConfig;

    // Register Service
    public RegisterResult register(String email, String password, String displayName, String avatarUrl) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(password);

        // set metadata
        request.setData(Map.of(
                "display_name", displayName != null ? displayName : email.split("@")[0],
                "avatar_url", avatarUrl != null ? avatarUrl : null));

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

    // Login Service
    public LoginResult login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setGrantType("password");

        try {
            LoginResponse response = supabaseWebClient
                    .post()
                    .uri("/token?grant_type=password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LoginResponse.class)
                    .block();

            if (response != null && response.getAccessToken() != null) {
                return new LoginResult(
                        true,
                        "Login successful",
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        response.getUser() != null ? response.getUser().getId() : null,
                        response.getUser() != null ? response.getUser().getEmail() : email);
            }
            return new LoginResult(false, "Login failed", null, null, null, email);

        } catch (WebClientResponseException.Unauthorized e) {
            throw new RuntimeException("Invalid email or password");
        } catch (WebClientResponseException e) {
            String errorMessage = "Login failed";
            String responseBody = e.getResponseBodyAsString();

            if (responseBody != null) {
                if (responseBody.contains("Invalid login credentials") ||
                        responseBody.contains("Invalid credentials")) {
                    errorMessage = "Invalid email or password";
                } else if (responseBody.contains("Email not confirmed")) {
                    errorMessage = "Email not confirmed. Please check your email for confirmation link.";
                }
            }

            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    // Update Profile Service
    public UserResult updateProfile(String accessToken, String displayName) {
        try {
            Map<String, Object> body = Map.of(
                    "data", Map.of(
                            "display_name", displayName));

            UserResponse response = supabaseWebClient
                    .put()
                    .uri("/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

            if (response != null) {
                return new UserResult(
                        true,
                        response.getId(),
                        response.getEmail(),
                        response.getDisplayName(),
                        response.getCreatedAt());
            }

            throw new RuntimeException("Failed to update profile");

        } catch (Exception e) {
            throw new RuntimeException("Update profile failed: " + e.getMessage());
        }
    }

    // Logout Service
    public LogoutResult logout(String accessToken) {
        try {
            String response = supabaseWebClient
                    .post()
                    .uri("/logout")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return new LogoutResult(true, "Logged out successfully");

        } catch (WebClientResponseException.Unauthorized e) {
            throw new RuntimeException("Invalid or expired token");
        } catch (WebClientResponseException e) {
            // Even if Supabase returns an error, we can consider logout successful
            // since the client will discard the token anyway
            return new LogoutResult(true, "Logged out successfully");
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    // Get User Service
    public UserResult getCurrentUser(String accessToken) {
        try {
            UserResponse response = supabaseWebClient
                    .get()
                    .uri("/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

            if (response != null) {
                return new UserResult(
                        true,
                        response.getId(),
                        response.getEmail(),
                        response.getDisplayName(),
                        response.getCreatedAt());
            }
            throw new RuntimeException("Failed to get user information");

        } catch (WebClientResponseException.Unauthorized e) {
            throw new RuntimeException("Invalid or expired token");
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to get user information: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user information: " + e.getMessage());
        }
    }

    // Register Request and Response
    @Data
    private static class RegisterRequest {
        private String email;
        private String password;
        @JsonProperty("data")
        private Map<String, Object> data; // additional user metadata
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

    // Login Request and Response
    @Data
    private static class LoginRequest {
        private String email;
        private String password;

        @JsonProperty("grant_type")
        private String grantType;
    }

    @Data
    private static class LoginResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        private User user;

        @Data
        private static class User {
            private String id;
            private String email;
            @JsonProperty("created_at")
            private String createdAt;
            @JsonProperty("email_confirmed_at")
            private String emailConfirmedAt;
        }
    }

    @Data
    public static class LoginResult {
        private boolean success;
        private String message;
        private String accessToken;
        private String refreshToken;
        private String userId;
        private String email;

        public LoginResult(boolean success, String message, String accessToken,
                String refreshToken, String userId, String email) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.email = email;
        }
    }

    // Logout Request and Response
    @Data
    public static class LogoutResult {
        private boolean success;
        private String message;

        public LogoutResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    // Get User Request and Response
    @Data
    private static class UserResponse {
        private String id;
        private String email;
        @JsonProperty("user_metadata")
        private UserMetadata userMetadata;
        @JsonProperty("created_at")
        private String createdAt;

        @Data
        private static class UserMetadata {
            @JsonProperty("display_name")
            private String displayName;
        }

        public String getDisplayName() {
            return userMetadata != null ? userMetadata.getDisplayName() : null;
        }
    }

    @Data
    public static class UserResult {
        private boolean success;
        private String id;
        private String email;
        private String displayName;
        private String createdAt;

        public UserResult(boolean success, String id, String email,
                String displayName, String createdAt) {
            this.success = success;
            this.id = id;
            this.email = email;
            this.displayName = displayName;
            this.createdAt = createdAt;
        }
    }
}