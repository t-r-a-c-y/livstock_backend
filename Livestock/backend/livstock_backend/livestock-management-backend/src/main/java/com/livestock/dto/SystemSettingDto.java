package com.livestock.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SystemSettingDto {

    private UUID id;

    private String organizationName;
    private String language;
    private String timezone;
    private String currency;
    private String dateFormat;
    private String theme;

    private String notificationEmail;

    private boolean emailReports;
    private boolean mobileNotifications;
    private boolean autoBackup;

    private int dataRetention; // in months

    private LocalDateTime updatedAt;
}