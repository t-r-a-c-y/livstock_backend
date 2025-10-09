package com.livestock.backend.dto;

import lombok.Data;

@Data
public class OwnerWithAnimalCountDTO {
    private OwnerDTO owner;
    private long animalCount;
}