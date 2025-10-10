package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProfileDTO {
    private UUID id;
    private String name;
    private String phone;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}