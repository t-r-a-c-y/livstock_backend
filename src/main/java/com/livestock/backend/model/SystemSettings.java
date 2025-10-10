package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
public class SystemSettings {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column
    private String currency;

    @Column
    private String timezone;

    @Column
    private String language;

    @Column(name = "date_format")
    private String dateFormat;

    @Column
    private String theme;

    @Column(name = "auto_backup")
    private boolean autoBackup;

    @Column(name = "data_retention")
    private Integer dataRetention;

    @Column(name = "email_reports")
    private boolean emailReports;

    @Column(name = "mobile_notifications")
    private boolean mobileNotifications;

    @Column(name = "notification_email")
    private String notificationEmail;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}