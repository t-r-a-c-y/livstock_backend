package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;



@Data
public class UserRoleUpdateDTO {
    private UUID userId;
    private String role;
}