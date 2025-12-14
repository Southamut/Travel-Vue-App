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

        @Query(value = "SELECT * FROM trips t " +
                        "WHERE " +
                        "(:query IS NULL OR :query = '' OR " +
                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                        "AND (array_length(:tags, 1) = 0 OR t.tags @> (:tags)::text[])\r\n" + //
                        " " +
                        "ORDER BY t.created_at DESC", countQuery = "SELECT COUNT(*) FROM trips t " +
                                        "WHERE " +
                                        "(:query IS NULL OR :query = '' OR " +
                                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                                        "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                                        "AND (array_length(:tags, 1) = 0 OR t.tags @> (:tags)::text[])\r\n" + //
                                        "", nativeQuery = true)
        Page<Trip> searchTrips(
                        @Param("query") String query,
                        @Param("tags") String[] tags,
                        Pageable pageable);

        @Query(value = "SELECT * FROM trips t " +
                        "WHERE t.author_id = :authorId " +
                        "AND (:query IS NULL OR :query = '' OR " +
                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                        "AND (array_length(:tags, 1) = 0 OR t.tags && (:tags)::text[]) " +
                        "ORDER BY t.created_at DESC", countQuery = "SELECT COUNT(*) FROM trips t " +
                                        "WHERE t.author_id = :authorId " +
                                        "AND (:query IS NULL OR :query = '' OR " +
                                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                                        "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                                        "AND (array_length(:tags, 1) = 0 OR t.tags && (:tags)::text[])", nativeQuery = true)
        Page<Trip> searchMyTrips(
                        @Param("authorId") Long authorId,
                        @Param("query") String query,
                        @Param("tags") String[] tags,
                        Pageable pageable);

        // Get all trips with pagination and author
        @EntityGraph(attributePaths = { "author" })
        Page<Trip> findByAuthorId(Long authorId, Pageable pageable);

        // Get trip by ID with author
        @EntityGraph(attributePaths = { "author" })
        Optional<Trip> findById(Long id);
}
