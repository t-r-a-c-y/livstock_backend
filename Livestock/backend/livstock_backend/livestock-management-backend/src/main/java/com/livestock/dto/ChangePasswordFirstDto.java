package com.livestock.dto;

import lombok.Data;

@Data
public class ChangePasswordFirstDto {
    private String currentPassword;
    private String newPassword;
}