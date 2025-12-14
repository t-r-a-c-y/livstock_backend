// src/main/java/com/livestock/dto/request/UserRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String name;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role; // admin, manager

    private String phone;
    private String avatar;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}