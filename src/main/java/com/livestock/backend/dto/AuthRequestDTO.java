package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String name;
    private String role;
}