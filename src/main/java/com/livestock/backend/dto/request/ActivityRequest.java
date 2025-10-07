package com.livestock.backend.dto.request;


import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityRequest {
    private String type;
    private String description;
    private Date date;
    private List<UUID> animalIds;
    private Integer amount;
    private Double cost;
    private String notes;
}