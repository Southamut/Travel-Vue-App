package com.techup.spring_demo.dto.user;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String avatarUrl;
}