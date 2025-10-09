package com.livestock.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String email;
    private String name;
    private String role;
}