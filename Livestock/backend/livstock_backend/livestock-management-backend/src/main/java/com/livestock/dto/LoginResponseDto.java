package com.livestock.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private UserDto user;
    private boolean mustChangePassword;  // ← add this field
}