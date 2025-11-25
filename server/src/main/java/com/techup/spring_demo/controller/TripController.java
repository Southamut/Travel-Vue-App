package com.techup.spring_demo.controller;

import com.techup.spring_demo.dto.TripPageResponse;
import com.techup.spring_demo.dto.TripRequest;
import com.techup.spring_demo.dto.TripResponse;
import com.techup.spring_demo.service.TripService;
import com.techup.spring_demo.service.SupabaseAuthService;
import com.techup.spring_demo.repository.UserRepository;
import com.techup.spring_demo.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;
    private final SupabaseAuthService supabaseAuthService;
    private final UserRepository userRepository;

    // Helper method to extract user ID from token
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized: No token provided");
        }
        String token = authorization.substring(7);

        try {
            SupabaseAuthService.UserResult user = supabaseAuthService.getCurrentUser(token);
            if (!user.isSuccess()) {
                throw new RuntimeException("Invalid token");
            }

            // Map Supabase email to local User ID, create if doesn't exist
            String email = user.getEmail();
            String displayName = user.getDisplayName() != null ? user.getDisplayName() : email.split("@")[0];

            // Find or create local user
            User localUser = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // Create new user if doesn't exist
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setPasswordHash("supabase_managed"); // Supabase handles password
                        newUser.setDisplayName(displayName);
                        return userRepository.save(newUser);
                    });

            return localUser.getId();
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    // Authenticated endpoints (must come before public /{id} to avoid route
    // conflict)

    // GET /api/trips/mine - Get user's trips
    @GetMapping("/mine")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = getUserIdFromToken(authorization);
        List<TripResponse> trips = tripService.getTripsByAuthor(userId);
        return ResponseEntity.ok(trips);
    }

    // Public API endpoints

    // GET /api/trips - Get all trips with pagination + search + tags
    @GetMapping
    public ResponseEntity<TripPageResponse> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> tags) {

        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasTags = tags != null && !tags.isEmpty();

        // ✅ Search by keyword + tags
        if (hasQuery || hasTags) {
            TripPageResponse result = tripService.searchTrips(
                    hasQuery ? query.trim() : null,
                    hasTags ? tags : null,
                    page,
                    size);
            return ResponseEntity.ok(result);
        }

        // ✅ Default: return all trips
        TripPageResponse result = tripService.getAllTrips(page, size);
        return ResponseEntity.ok(result);
    }

    // GET /api/trips/{id} - Get trip by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long id) {
        TripResponse trip = tripService.getTripById(id);
        return ResponseEntity.ok(trip);
    }

    // POST /api/trips - Create new trip
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody TripRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = getUserIdFromToken(authorization);
        TripResponse trip = tripService.createTrip(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    // PUT /api/trips/{id} - Update trip
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = getUserIdFromToken(authorization);
        TripResponse trip = tripService.updateTrip(id, request, userId);
        return ResponseEntity.ok(trip);
    }

    // DELETE /api/trips/{id} - Delete trip
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = getUserIdFromToken(authorization);
        tripService.deleteTrip(id, userId);
        return ResponseEntity.noContent().build();
    }
}
