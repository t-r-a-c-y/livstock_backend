package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type;
    private String description;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}