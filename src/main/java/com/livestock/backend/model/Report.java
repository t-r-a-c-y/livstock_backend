package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String type; // Livestock, Financial, etc.
    private String status = "Processing";
    private Date dateFrom;
    private Date dateTo;
    private String data; // JSON string for report data

    @ManyToOne
    @JoinColumn(name = "generated_by")
    private UserProfile generatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}