package com.livestock.backend.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AnimalRequest {
    @NotBlank
    private String tagId;
    @NotBlank
    private String type;
    private String breed;
    private String gender;
    private Date dateOfBirth;
    private String status;
    private UUID ownerId;
    private UUID parentId;
    private Double milkProduction;
    private String photo;
    private String notes;
}