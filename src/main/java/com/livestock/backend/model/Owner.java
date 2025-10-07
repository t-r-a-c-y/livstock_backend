package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "owners")
@Data
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "owner")
    private List<Animal> animals;

    @OneToMany(mappedBy = "owner")
    private List<FinancialRecord> financialRecords;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}