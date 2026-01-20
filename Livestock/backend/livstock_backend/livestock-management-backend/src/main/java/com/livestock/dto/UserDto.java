package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean isActive;
    private String avatar;
    private String phone;
    private LocalDateTime lastLogin;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}