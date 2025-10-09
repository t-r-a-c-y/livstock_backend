package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "system_settings")
public class SystemSetting {
    @Id
    private String key;

    @Column(nullable = false)
    private String value;
}