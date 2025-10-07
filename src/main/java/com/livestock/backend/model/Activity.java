package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
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
    private Date date;

    @ManyToMany
    @JoinTable(
            name = "activity_animals",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private List<Animal> animals;

    private Integer amount;
    private Double cost;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserProfile createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}