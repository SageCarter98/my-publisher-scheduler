package com.mps.organization.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 200)
    private String name;
    @Column(nullable = false, length = 64)
    private String timezone = "UTC";
    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Organization() {}
    public Organization(String name, String timezone) { this.name = name; this.timezone = timezone; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getTimezone() { return timezone; }
    public String getStatus() { return status; }
    public void update(String name, String timezone) { this.name = name.trim(); this.timezone = timezone; this.updatedAt = Instant.now(); }
}
