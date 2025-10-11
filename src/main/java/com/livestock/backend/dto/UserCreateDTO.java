package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Data
public class UserCreateDTO {
    private String email; // For Supabase auth
    private String password; // For Supabase auth
    private String name;
    private String phone;
    private String avatar;
    private List<String> roles;
}