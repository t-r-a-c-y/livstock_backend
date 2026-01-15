package com.livestock.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OwnerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}