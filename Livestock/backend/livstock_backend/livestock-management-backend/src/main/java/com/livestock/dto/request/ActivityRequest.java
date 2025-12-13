// src/main/java/com/livestock/dto/request/ActivityRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ActivityRequest {
    @NotNull
    private List<UUID> animalIds;

    @NotBlank
    private String type;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime date;

    private BigDecimal amount;

    private BigDecimal cost;

    private String notes;

    @NotBlank
    private String createdBy;

    // --- EXPLICIT GETTERS ---
    public List<UUID> getAnimalIds() {
        return animalIds;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    // Setters if needed (ModelMapper uses them)
    public void setAnimalIds(List<UUID> animalIds) {
        this.animalIds = animalIds;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}