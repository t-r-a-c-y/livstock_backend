package com.example.livestock.repository;

import com.example.livestock.entity.ReportLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportLogRepository extends JpaRepository<ReportLog, Long> {
    Page<ReportLog> findByActiveTrue(Pageable pageable);
}
