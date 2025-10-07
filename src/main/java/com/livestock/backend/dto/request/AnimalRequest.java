package com.livestock.backend.dto.request;

import lombok.Data;

@Data
public class AnimalRequest {
    private String tagId;
    private String species;
    private String breed;
    private String gender;
    private String healthStatus;
}