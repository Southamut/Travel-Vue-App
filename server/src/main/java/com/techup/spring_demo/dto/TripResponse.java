package com.techup.spring_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    
    private Long id;
    private String title;
    private String description;
    private List<String> photos = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Double latitude;
    private Double longitude;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

