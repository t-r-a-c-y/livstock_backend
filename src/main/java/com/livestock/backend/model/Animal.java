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
    private String species;
    private String breed;
    private String gender;
    private String healthStatus;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "animal")
    private List<Activity> activities;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}