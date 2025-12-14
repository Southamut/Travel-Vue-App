package com.techup.spring_demo.controller;

import com.techup.spring_demo.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

  private final SupabaseStorageService supabaseStorageService;

  @PostMapping("/upload")
  public ResponseEntity<Map<String, String>> upload(
      @RequestParam("file") MultipartFile file,
      @RequestHeader("Authorization") String authHeader) {
    
    // Validation
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
    }
    
    String contentType = file.getContentType();
    if (!isImageFile(contentType)) {
      return ResponseEntity.badRequest().body(Map.of("error", "Only image files allowed"));
    }
    
    long maxSize = 5 * 1024 * 1024; // 5MB
    if (file.getSize() > maxSize) {
      return ResponseEntity.badRequest().body(Map.of("error", "File size exceeds 5MB limit"));
    }
    
    String userToken = authHeader.replace("Bearer ", "");
    String url = supabaseStorageService.uploadFile(file, userToken);
    return ResponseEntity.ok(Map.of("url", url));
  }

  private boolean isImageFile(String contentType) {
    return contentType != null && (
        contentType.equals("image/jpeg") ||
        contentType.equals("image/png") ||
        contentType.equals("image/gif") ||
        contentType.equals("image/webp")
    );
  }
}