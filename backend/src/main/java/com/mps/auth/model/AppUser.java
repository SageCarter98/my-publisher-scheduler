package com.mps.auth.model;

import com.mps.organization.model.Organization;
import com.mps.organization.model.Department;
import com.mps.organization.model.MemberGroup;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(name = "uq_user_org_email", columnNames = {"organization_id", "email"}))
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(nullable = false, length = 320)
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private MemberGroup group;
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserStatus status = UserStatus.PENDING_ACTIVATION;
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;
    @Column(name = "locked_until")
    private Instant lockedUntil;
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    protected AppUser() {}
    public AppUser(Organization organization, String email, String passwordHash, String firstName, String lastName) {
        this.organization = organization; this.email = email.toLowerCase(); this.passwordHash = passwordHash;
        this.firstName = firstName; this.lastName = lastName; this.status = UserStatus.ACTIVE;
        this.passwordChangedAt = Instant.now();
    }
    public UUID getId() { return id; }
    public Organization getOrganization() { return organization; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public UserStatus getStatus() { return status; }
    public Set<Role> getRoles() { return roles; }
    public Department getDepartment() { return department; }
    public MemberGroup getGroup() { return group; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public Instant getLockedUntil() { return lockedUntil; }
    public void addRole(Role role) { roles.add(role); }
    public void replaceRoles(Set<Role> replacement) { roles.clear(); roles.addAll(replacement); updatedAt = Instant.now(); }
    public void updateProfile(String firstName, String lastName) { this.firstName = firstName.trim(); this.lastName = lastName.trim(); updatedAt = Instant.now(); }
    public void setAssociations(Department department, MemberGroup group) {
        if (group != null && department != null && group.getDepartment() != null && !group.getDepartment().getId().equals(department.getId())) throw new IllegalArgumentException("Group does not belong to the selected department.");
        this.department = department; this.group = group; updatedAt = Instant.now();
    }
    public void archive() { status = UserStatus.ARCHIVED; updatedAt = Instant.now(); }
    public void restore() { status = UserStatus.ACTIVE; lockedUntil = null; failedLoginAttempts = 0; updatedAt = Instant.now(); }
    public void recordSuccessfulLogin() { failedLoginAttempts = 0; lockedUntil = null; lastLoginAt = Instant.now(); updatedAt = Instant.now(); }
    public void recordFailedLogin(int maxAttempts, long lockMinutes) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= maxAttempts) { lockedUntil = Instant.now().plusSeconds(lockMinutes * 60); status = UserStatus.LOCKED; }
        updatedAt = Instant.now();
    }
    public void unlockIfExpired() {
        if (status == UserStatus.LOCKED && lockedUntil != null && lockedUntil.isBefore(Instant.now())) {
            status = UserStatus.ACTIVE; lockedUntil = null; failedLoginAttempts = 0; updatedAt = Instant.now();
        }
    }
}
