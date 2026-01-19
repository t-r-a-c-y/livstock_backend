package com.livestock.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;



@Entity
@Table(name = "animals")
@Data
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String tagId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType type;

    @Column(length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Animal parent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal milkProduction;

    @Column(length = 500)
    private String photo;           // URL or file path

    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToMany(mappedBy = "animals")
    private Set<Activity> activities = new HashSet<>();

    @OneToMany(mappedBy = "animal")
    private List<FinancialRecord> financialRecords;
}