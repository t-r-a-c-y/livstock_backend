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
    private String address;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private String notes;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Animal> animals;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<FinancialRecord> financialRecords;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}