package com.livestock.backend.dto.request;


import lombok.Data;

@Data
public class OwnerRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private String notes;
}