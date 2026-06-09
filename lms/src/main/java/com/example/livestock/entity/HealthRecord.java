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
public class HealthRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private String diagnosis;
    private String treatment;
    private String medication;
    private String veterinarianName;
    @Column(nullable = false)
    private LocalDate visitDate;
    private LocalDate nextVisitDate;
    @Column(length = 2000)
    private String notes;
}
