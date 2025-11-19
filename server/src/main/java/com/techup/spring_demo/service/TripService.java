package com.techup.spring_demo.service;

import com.techup.spring_demo.dto.TripPageResponse;
import com.techup.spring_demo.dto.TripRequest;
import com.techup.spring_demo.dto.TripResponse;
import com.techup.spring_demo.entity.Trip;
import com.techup.spring_demo.repository.TripRepository;
import com.techup.spring_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {
    
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    
    public List<TripResponse> getTripsByAuthor(Long authorId) {
        List<Trip> trips = tripRepository.findByAuthorId(authorId);
        return trips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TripResponse createTrip(TripRequest request, Long authorId) {
        // Verify user exists
        userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Trip trip = new Trip();
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        trip.setPhotos(request.getPhotos() != null ? request.getPhotos() : new ArrayList<>());
        trip.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        trip.setLatitude(request.getLatitude());
        trip.setLongitude(request.getLongitude());
        trip.setAuthorId(authorId);
        
        Trip savedTrip = tripRepository.save(trip);
        return mapToResponse(savedTrip);
    }
    
    @Transactional
    public TripResponse updateTrip(Long tripId, TripRequest request, Long authorId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        // Check ownership
        if (!trip.getAuthorId().equals(authorId)) {
            throw new RuntimeException("You don't have permission to edit this trip");
        }
        
        trip.setTitle(request.getTitle());
        trip.setDescription(request.getDescription());
        if (request.getPhotos() != null) {
            trip.setPhotos(request.getPhotos());
        }
        if (request.getTags() != null) {
            trip.setTags(request.getTags());
        }
        trip.setLatitude(request.getLatitude());
        trip.setLongitude(request.getLongitude());
        
        Trip updatedTrip = tripRepository.save(trip);
        return mapToResponse(updatedTrip);
    }
    
    @Transactional
    public void deleteTrip(Long tripId, Long authorId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        // Check ownership
        if (!trip.getAuthorId().equals(authorId)) {
            throw new RuntimeException("You don't have permission to delete this trip");
        }
        
        tripRepository.delete(trip);
    }
    
    // Public API methods
    public TripPageResponse getAllTrips(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> tripPage = tripRepository.findAll(pageable);
        
        List<TripResponse> content = tripPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new TripPageResponse(
                content,
                tripPage.getNumber(),
                tripPage.getSize(),
                tripPage.getTotalElements(),
                tripPage.getTotalPages(),
                tripPage.hasNext(),
                tripPage.hasPrevious()
        );
    }
    
    public TripPageResponse searchTrips(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> tripPage = tripRepository.searchTrips(query, pageable);
        
        List<TripResponse> content = tripPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new TripPageResponse(
                content,
                tripPage.getNumber(),
                tripPage.getSize(),
                tripPage.getTotalElements(),
                tripPage.getTotalPages(),
                tripPage.hasNext(),
                tripPage.hasPrevious()
        );
    }
    
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return mapToResponse(trip);
    }
    
    private TripResponse mapToResponse(Trip trip) {
        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        response.setTitle(trip.getTitle());
        response.setDescription(trip.getDescription());
        response.setPhotos(trip.getPhotos() != null ? trip.getPhotos() : new ArrayList<>());
        response.setTags(trip.getTags() != null ? trip.getTags() : new ArrayList<>());
        response.setLatitude(trip.getLatitude());
        response.setLongitude(trip.getLongitude());
        response.setAuthorId(trip.getAuthorId());
        response.setAuthorName(trip.getAuthor() != null ? trip.getAuthor().getDisplayName() : null);
        response.setCreatedAt(trip.getCreatedAt());
        response.setUpdatedAt(trip.getUpdatedAt());
        return response;
    }
}

