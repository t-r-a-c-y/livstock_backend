// src/main/java/com/livestock/dto/response/LoginResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String avatar;
    private String token;
}