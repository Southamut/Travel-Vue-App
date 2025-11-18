package com.techup.spring_demo.repository;

import com.techup.spring_demo.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByAuthorId(Long authorId);
}

