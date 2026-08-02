package com.mps.organization.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "department", uniqueConstraints = @UniqueConstraint(name = "uq_department_org_name", columnNames = {"organization_id", "name"}))
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false) private Organization organization;
    @Column(nullable = false, length = 160) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false, length = 32) private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    @Column(name = "archived_at") private Instant archivedAt;
    protected Department() {}
    public Department(Organization organization, String name, String description) { this.organization=organization; this.name=name.trim(); this.description=description; }
    public UUID getId(){return id;} public Organization getOrganization(){return organization;} public String getName(){return name;} public String getDescription(){return description;} public String getStatus(){return status;}
    public void update(String name,String description){this.name=name.trim();this.description=description;this.updatedAt=Instant.now();}
    public void archive(){this.status="ARCHIVED";this.archivedAt=Instant.now();this.updatedAt=Instant.now();}
}
