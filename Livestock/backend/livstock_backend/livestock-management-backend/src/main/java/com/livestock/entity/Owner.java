package com.livestock.entity;// Owner.java


import com.livestock.entity.FinancialRecord;
import com.livestock.entity.enums.OwnerStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "owners")
@Data
public class Owner {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerStatus status = OwnerStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User linkedUser;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 500)
    private String avatar;

    @Column(length = 50)
    private String nationalId;

    @Column(length = 100)
    private String bankAccount;

    @Column(length = 30)
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animals = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<FinancialRecord> financialRecords = new ArrayList<>();

}