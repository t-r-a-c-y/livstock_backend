package com.example.livestock.entity;

import com.example.livestock.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class VaccinationRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private String vaccineName;
    @Column(nullable = false)
    private LocalDate vaccinationDate;
    private LocalDate nextDueDate;
    private String administeredBy;
    @Column(length = 2000)
    private String notes;
}
