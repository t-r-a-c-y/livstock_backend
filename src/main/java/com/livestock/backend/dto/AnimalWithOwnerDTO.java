package com.livestock.backend.dto;

import lombok.Data;

@Data
public class AnimalWithOwnerDTO {
    private AnimalDTO animal;
    private OwnerDTO owner;
}