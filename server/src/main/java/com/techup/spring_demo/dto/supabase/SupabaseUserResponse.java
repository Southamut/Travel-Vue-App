package com.techup.spring_demo.dto.supabase;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SupabaseUserResponse {
    private String id;
    private String email;

    @JsonProperty("user_metadata")
    private UserMetadata userMetadata;

    @JsonProperty("created_at")
    private String createdAt;

    @Data
    public static class UserMetadata {
        @JsonProperty("display_name")
        private String displayName;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    public String getDisplayName() {
        return userMetadata != null ? userMetadata.getDisplayName() : null;
    }

    public String getAvatarUrl() {
        return userMetadata != null ? userMetadata.getAvatarUrl() : null;
    }
}
