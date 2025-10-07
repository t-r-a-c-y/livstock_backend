package com.livestock.backend.dto.response;

import com.livestock.backend.model.UserRole;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private String status;
    private String phone;
    private Date createdAt;
}
