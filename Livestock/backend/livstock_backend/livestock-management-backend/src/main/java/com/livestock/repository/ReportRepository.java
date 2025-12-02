// ReportRepository.java
package com.livestock.repository;

import com.livestock.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByTypeOrderByCreatedAtDesc(String type);

    List<Report> findTop10ByOrderByCreatedAtDesc();

    List<Report> findByGeneratedByOrderByCreatedAtDesc(String generatedBy);
}