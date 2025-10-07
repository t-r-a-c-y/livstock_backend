package com.livestock.backend.dto.response;


import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class AnimalResponse {
    private UUID id;
    private String tagId;
    private String type;
    private String breed;
    private String gender;
    private Date dateOfBirth;
    private String status;
    private OwnerSummary owner;
    private AnimalSummary parent;
    private Double milkProduction;
    private String photo;
    private String notes;
    private List<ActivitySummary> activities;
    private Date createdAt;
    private Date updatedAt;

    @Data
    public static class OwnerSummary {
        private UUID id;
        private String name;
        private String email;
        private String phone;
    }

    @Data
    public static class AnimalSummary {
        private UUID id;
        private String tagId;
    }

    @Data
    public static class ActivitySummary {
        private UUID id;
        private String type;
    }
}