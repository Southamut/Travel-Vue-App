package com.techup.spring_demo.dto.supabase;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SupabaseLoginRequest {
    private String email;
    private String password;

    @JsonProperty("grant_type")
    private String grantType;
}
