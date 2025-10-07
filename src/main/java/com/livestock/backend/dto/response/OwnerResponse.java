package com.livestock.backend.dto.response;


import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OwnerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private List<AnimalSummary> animals;
    private int totalAnimals;
    private Date createdAt;

    @Data
    public static class AnimalSummary {
        private UUID id;
        private String tagId;
        private String type;
        private String status;
    }
}