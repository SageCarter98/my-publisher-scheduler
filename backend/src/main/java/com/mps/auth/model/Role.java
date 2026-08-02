package com.mps.auth.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 64)
    private String code;
    @Column(nullable = false, length = 120)
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
    protected Role() {}
    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Set<Permission> getPermissions() { return permissions; }
}
