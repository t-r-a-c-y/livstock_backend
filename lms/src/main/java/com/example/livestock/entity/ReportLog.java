package com.example.livestock.entity;

import com.example.livestock.audit.BaseEntity;
import com.example.livestock.enums.ExportFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ReportLog extends BaseEntity {
    @Column(nullable = false)
    private String reportName;

    @Column(nullable = false)
    private String reportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExportFormat exportFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_id", nullable = false)
    private User generatedBy;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private String filePath;
}
