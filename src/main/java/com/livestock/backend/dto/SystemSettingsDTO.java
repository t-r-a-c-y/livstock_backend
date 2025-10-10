package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SystemSettingsDTO {
    private UUID id;
    private String organizationName;
    private String currency;
    private String timezone;
    private String language;
    private String dateFormat;
    private String theme;
    private boolean autoBackup;
    private Integer dataRetention;
    private boolean emailReports;
    private boolean mobileNotifications;
    private String notificationEmail;
    private LocalDateTime updatedAt;
}