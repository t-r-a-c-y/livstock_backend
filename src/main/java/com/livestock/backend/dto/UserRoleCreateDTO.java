package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;



@Data
public class UserRoleCreateDTO {
    private UUID userId;
    private String role;
}
