
package com.livestock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "animals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tag_id", unique = true, nullable = false, length = 100)
    private String tagId;

    @Column(nullable = false, length = 50)
    private String type; // cow, calf, goat, kid

    @Column(nullable = false, length = 100)
    private String breed;

    @Column(nullable = false, length = 10)
    private String gender; // male, female

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(nullable = false, length = 50)
    @ColumnDefault("'healthy'")
    private String status = "healthy"; // healthy, sick, sold, dead

    @Column(precision = 10, scale = 2)
    private BigDecimal milk;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Animal parent;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Many-to-many with Activity via junction table
    @ManyToMany
    @JoinTable(
            name = "activity_animals",
            joinColumns = @JoinColumn(name = "animal_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private List<Activity> activities = new ArrayList<>();
}