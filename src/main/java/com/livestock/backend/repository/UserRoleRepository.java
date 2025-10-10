package com.livestock.backend.repository;


import com.livestock.backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    // Find roles by userId
    @Query("SELECT r FROM UserRole r WHERE r.userId = :userId")
    List<UserRole> findByUserId(@Param("userId") UUID userId);
}