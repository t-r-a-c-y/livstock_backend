package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String role;
}