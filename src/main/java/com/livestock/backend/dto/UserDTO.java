package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String phone;
    private String avatar;
    private List<String> roles; // From user_roles
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
