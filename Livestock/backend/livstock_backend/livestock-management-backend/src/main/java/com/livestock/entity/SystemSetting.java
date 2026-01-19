// SystemSetting.java
package com.livestockmis.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 150)
    private String organizationName = "LivestockMIS Farm";

    @Column(length = 10)
    private String language = "en";

    @Column(length = 50)
    private String timezone = "UTC-05:00";

    @Column(length = 10)
    private String currency = "USD";

    @Column(length = 20)
    private String dateFormat = "MM/DD/YYYY";

    @Column(length = 20)
    private String theme = "light";

    @Column(length = 120)
    private String notificationEmail;

    @Column(nullable = false)
    private boolean emailReports = true;

    @Column(nullable = false)
    private boolean mobileNotifications = true;

    @Column(nullable = false)
    private boolean autoBackup = true;

    @Column(nullable = false)
    private int dataRetention = 12; // in months

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}