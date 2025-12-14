package com.techup.spring_demo.dto.supabase;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SupabaseRegisterResponse {
    private User user;
    private String access_token;
    private String refresh_token;

    @Data
    public static class User {
        private String id;
        private String email;
        @JsonProperty("created_at")
        private String createdAt;
    }
}
