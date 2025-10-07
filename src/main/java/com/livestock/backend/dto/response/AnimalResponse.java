package com.livestock.backend.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AnimalResponse {
    private UUID id;
    private String tagId;
    private String species;
    private String breed;
    private String gender;
    private String healthStatus;
    private OwnerSummary owner;
    private Date createdAt;

    @Data
    public static class OwnerSummary {
        private UUID id;
        private String name;
        private String email;
        private String phone;
    }
}