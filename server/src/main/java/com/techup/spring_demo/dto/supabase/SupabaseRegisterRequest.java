package com.techup.spring_demo.dto.supabase;

import lombok.Data;
import java.util.Map;

@Data
public class SupabaseRegisterRequest {
    private String email;
    private String password;
    private Map<String, Object> data;
}
