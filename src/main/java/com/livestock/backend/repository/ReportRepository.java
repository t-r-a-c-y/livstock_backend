package com.livestock.backend.repository;

import com.livestock.backend.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    // Find all reports
    @Query("SELECT r FROM Report r")
    List<Report> findAllActive();

    // Paginated version
    @Query("SELECT r FROM Report r")
    Page<Report> findAllActive(Pageable pageable);

    // Filter by type
    @Query("SELECT r FROM Report r WHERE r.type = :type")
    List<Report> findByType(@Param("type") String type);
}