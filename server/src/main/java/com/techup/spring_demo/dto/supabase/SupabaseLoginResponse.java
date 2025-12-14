package com.techup.spring_demo.dto.supabase;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SupabaseLoginResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private User user;

    @Data
    public static class User {
        private String id;
        private String email;
        @JsonProperty("created_at")
        private String createdAt;
        @JsonProperty("email_confirmed_at")
        private String emailConfirmedAt;
    }
}
