package com.techup.spring_demo.repository;

import com.techup.spring_demo.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByAuthorId(Long authorId);
    
    // Search trips by title or description (case-insensitive) with author
    @Query("SELECT t FROM Trip t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    @EntityGraph(attributePaths = {"author"})
    Page<Trip> searchTrips(@Param("query") String query, Pageable pageable);
    
    // Get all trips with pagination and author
    @EntityGraph(attributePaths = {"author"})
    Page<Trip> findAll(Pageable pageable);
    
    // Get trip by ID with author
    @EntityGraph(attributePaths = {"author"})
    Optional<Trip> findById(Long id);
}

