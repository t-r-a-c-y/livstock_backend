// src/main/java/com/livestock/dto/request/OwnerRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OwnerRequest {
    @NotBlank
    private String name;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String phone;

    private String address;
    private String avatar;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private String notes;
}