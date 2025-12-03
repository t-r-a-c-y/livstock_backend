// src/main/java/com/livestock/dto/response/UserResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String avatar;
    private String status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}