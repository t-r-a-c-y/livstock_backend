package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;



@Data
public class SystemSettingCreateDTO {
    private String organizationName;
    private String currency;
    private String timezone;
    private String language;
    private String dateFormat;
    private String theme;
    private Boolean autoBackup;
    private Integer dataRetention;
    private Boolean emailReports;
    private Boolean mobileNotifications;
    private String notificationEmail;
}

