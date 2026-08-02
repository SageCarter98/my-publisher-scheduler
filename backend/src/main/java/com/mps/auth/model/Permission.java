package com.mps.auth.model;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name = "permission")
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 120) private String code;
    @Column(nullable = false, length = 500) private String description;
    protected Permission() {}
    public String getCode() { return code; }
}
