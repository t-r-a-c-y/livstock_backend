package com.example.livestock.entity;

import com.example.livestock.audit.BaseEntity;
import com.example.livestock.enums.PregnancyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class BreedingRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cow_id", nullable = false)
    private Animal cow;

    @Column(nullable = false)
    private LocalDate matingDate;

    private String maleAnimalUsed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PregnancyStatus pregnancyStatus = PregnancyStatus.PENDING;

    private LocalDate expectedBirthDate;
    private LocalDate actualBirthDate;
    @Column(length = 2000)
    private String notes;
}
