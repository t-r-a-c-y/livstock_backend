package com.livestock.backend.repository;

import com.livestock.backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    // Find all profiles (no soft delete in schema)
    @Query("SELECT p FROM Profile p")
    List<Profile> findAllActive();
}