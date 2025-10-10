package com.livestock.backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
public class SystemSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "currency")
    private String currency;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "language")
    private String language;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "theme")
    private String theme;

    @Column(name = "auto_backup")
    private Boolean autoBackup;

    @Column(name = "data_retention")
    private Integer dataRetention;

    @Column(name = "email_reports")
    private Boolean emailReports;

    @Column(name = "mobile_notifications")
    private Boolean mobileNotifications;

    @Column(name = "notification_email")
    private String notificationEmail;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}