package com.livestock.backend.dto.request;

import com.livestock.backend.model.UserRole;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
}