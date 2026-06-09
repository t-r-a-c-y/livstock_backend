package com.example.livestock.service;

import com.example.livestock.dto.ReportLogResponse;
import com.example.livestock.dto.ReportRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ReportService {
    ResponseEntity<Resource> export(ReportRequest request);
    Page<ReportLogResponse> logs(Pageable pageable);
}
