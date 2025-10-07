package com.livestock.backend.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityResponse {
    private UUID id;
    private String type;
    private String description;
    private Date date;
    private List<AnimalSummary> animals;
    private Integer amount;
    private Double cost;
    private String notes;
    private String createdBy;
    private Date createdAt;

    @Data
    public static class AnimalSummary {
        private UUID id;
        private String tagId;
    }
}
