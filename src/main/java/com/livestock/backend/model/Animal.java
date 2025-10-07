package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "animals")
@Data
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String tagId;
    private String type;
    private String breed;
    private String gender;
    private Date dateOfBirth;
    private String status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Animal parent;

    private Double milkProduction;
    private String photo;
    private String notes;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<FinancialRecord> financialRecords;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}